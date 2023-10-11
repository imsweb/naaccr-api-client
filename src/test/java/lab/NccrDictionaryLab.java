/*
 * Copyright (C) 2023 Information Management Services, Inc.
 */
package lab;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.imsweb.naaccr.api.client.NaaccrApiClient;
import com.imsweb.naaccr.api.client.NaaccrApiException;
import com.imsweb.naaccr.api.client.entity.NaaccrAllowedCode;
import com.imsweb.naaccr.api.client.entity.NaaccrDataItem;

public class NccrDictionaryLab {

    public static void main(String[] args) throws IOException {
        NaaccrApiClient client = NaaccrApiClient.getInstance();

        File requiredFields = new File("C:\\dev\\temp\\nccr\\nccr.CTC.platform.variables.20230831.csv");
        File ddFile = new File("C:\\dev\\temp\\nccr\\seer.22reg.yr1973_2020.dd");

        // possible Semantic_Type:  "numeric", "boolean", "string", "lookup_value", "numeric_relation"

        String version = "23";
        Map<String, List<String>> itemIds = new LinkedHashMap<>();
        try (LineNumberReader reader = new LineNumberReader(new InputStreamReader(new FileInputStream(requiredFields), StandardCharsets.US_ASCII))) {
            String line = reader.readLine();
            while (line != null) {
                List<String> parts = parseCsvLine(reader.getLineNumber(), line);
                if (parts.size() >= 4 && !parts.get(3).isEmpty() && !"XML NAACCR Id".equalsIgnoreCase(parts.get(3)))
                    itemIds.put(parts.get(3), parts);
                line = reader.readLine();
            }
        }
        Set<String> itemsToFetch = new LinkedHashSet<>(itemIds.keySet());

        Map<String, Map<String, String>> ddFileInfo = readDdFile(ddFile);

        NccrDictionary dictionary = new NccrDictionary();
        dictionary.setElements(new ArrayList<>());

        List<String> unknown = new ArrayList<>();
        for (String itemId : itemsToFetch) {
            System.out.println(itemId);

            List<String> itemInfo = itemIds.get(itemId);

            NccrDictionaryElement element = null;
            try {
                element = createElementFromApi(client.getDataItem(version, itemId), version);
                System.out.println("  > API");
            }
            catch (NaaccrApiException e) {
                Map<String, String> ddField = ddFileInfo.values().stream()
                        .filter(m -> itemId.equals(m.get("naaccrId")) || (itemInfo.get(2) != null && itemInfo.get(2).equals(m.get("FieldName"))))
                        .findFirst()
                        .orElse(null);
                if (ddField != null) {
                    element = createElementFromDdFile(ddField, ddFileInfo.get("Format=" + ddField.get("FieldName")));
                    System.out.println("  > DD File");
                }
                else {
                    unknown.add(itemId);
                    System.out.println(" > !!!! unknown");
                }
            }

            if (element != null) {
                String applicableYears = itemInfo.get(4);
                if (applicableYears != null && !applicableYears.isEmpty())
                    element.setRationale("Years Applicable: " + applicableYears);
                dictionary.getElements().add(element);
            }

        }

        //for (String s : unknown)
        //    System.out.println(s);

        System.out.println(getMapper().writeValueAsString(dictionary));
    }

    private static Map<String, Map<String, String>> readDdFile(File file) throws IOException {
        Map<String, Map<String, String>> result = new LinkedHashMap<>();

        boolean inSection = false;
        Map<String, String> currentSection = null;
        for (String line : Files.readAllLines(file.toPath())) {
            if (line.isEmpty())
                inSection = false;
            else if (line.startsWith("[") && !inSection) {
                currentSection = new LinkedHashMap<>();
                result.put(line.substring(1, line.length() - 1), currentSection);
                inSection = true;
            }
            else if (inSection) {
                int idx = line.indexOf('=');
                String value = line.substring(idx + 1);
                if (value.startsWith("\"") && value.endsWith("\""))
                    value = value.substring(1, value.length() - 1);
                currentSection.put(line.substring(0, idx), value);
            }
        }

        return result;
    }

    private static NccrDictionaryElement createElementFromApi(NaaccrDataItem item, String version) {
        NccrDictionaryElement element = new NccrDictionaryElement();
        element.setColumnNameAtSource(item.getXmlNaaccrId());
        NccrDictionaryDataType dataType = new NccrDictionaryDataType();
        dataType.setFieldLength(item.getItemLength());
        if (item.getAllowedCodes() != null && !item.getAllowedCodes().isEmpty())
            dataType.setSemanticType("lookup_value");
        else if ("digits".equals(item.getItemDataType()))
            dataType.setSemanticType("numeric");
        else
            dataType.setSemanticType("string");
        if (item.getSourceOfStandard() != null)
            dataType.setAbbreviatedSourceVocabulary(item.getSourceOfStandard());
        element.setColumnDataTypeAtSource(dataType);
        element.setItemName(item.getItemName());
        element.setItemId(item.getXmlNaaccrId());
        element.setItemNumber(item.getItemNumber().toString());
        element.setInternetLink("https://apps.naaccr.org/data-dictionary/api/1.0/data_item/" + version + "/" + item.getXmlNaaccrId());
        element.setItemDescription(item.getDescription());

        if (item.getAllowedCodes() != null) {
            List<NccrDictionaryElementValue> values = new ArrayList<>();
            for (NaaccrAllowedCode code : item.getAllowedCodes()) {
                if (code.getCode() != null && !code.getCode().isEmpty() && code.getDescription() != null && !code.getDescription().isEmpty()) {
                    NccrDictionaryElementValue value = new NccrDictionaryElementValue();
                    value.setValue(code.getCode());
                    value.setDescription(code.getDescription());
                    values.add(value);
                }
            }
            if (!values.isEmpty())
                element.setPermissibleValues(values);
        }

        return element;
    }

    private static NccrDictionaryElement createElementFromDdFile(Map<String, String> field, Map<String, String> lookup) {
        NccrDictionaryElement element = new NccrDictionaryElement();
        element.setColumnNameAtSource(field.get("naaccrId"));
        NccrDictionaryDataType dataType = new NccrDictionaryDataType();
        dataType.setFieldLength(Integer.valueOf(field.get("Length")));
        if (lookup != null)
            dataType.setSemanticType("lookup_value");
        else
            dataType.setSemanticType("string");
        dataType.setAbbreviatedSourceVocabulary("SEER Recode");
        element.setColumnDataTypeAtSource(dataType);
        element.setItemName(field.get("FieldName"));
        element.setItemId(field.get("naaccrId"));
        element.setItemNumber(field.get("NAACCRItemNumber"));
        element.setInternetLink(null);
        element.setItemDescription(null);

        if (lookup != null) {
            List<NccrDictionaryElementValue> values = new ArrayList<>();
            for (Entry<String, String> entry : lookup.entrySet()) {
                NccrDictionaryElementValue value = new NccrDictionaryElementValue();
                value.setValue(entry.getKey());
                value.setDescription(entry.getValue());
                values.add(value);
            }
            if (!values.isEmpty())
                element.setPermissibleValues(values);
        }

        return element;
    }

    private static ObjectMapper getMapper() {
        ObjectMapper mapper = new ObjectMapper();

        // do not write null values
        mapper.setSerializationInclusion(Include.NON_NULL);

        // annotations are set on the fields (not the getters/setters)
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

        return mapper;
    }

    public static List<String> parseCsvLine(int lineNumber, String line) throws IOException {
        List<String> result = new ArrayList<>();

        char cQuote = '"';
        char cDelimiter = ',';
        int curIndex = 0;
        int nextQuote;
        int nextDelimiter;

        StringBuilder buf = new StringBuilder();
        buf.append(cQuote);
        String singleQuotes = buf.toString();
        buf.append(cQuote);
        String doubleQuotes = buf.toString();

        String value;
        while (curIndex < line.length()) {
            if (line.charAt(curIndex) == cQuote) {
                // handle quoted value
                nextQuote = getNextSingleQuote(line, cQuote, curIndex);
                if (nextQuote < 0)
                    throw new IOException("Line " + lineNumber + ": found an unmatched quote");
                else {
                    result.add(line.substring(curIndex + 1, nextQuote).replace(doubleQuotes, singleQuotes));
                    // update the current index to be after delimiter, after the ending quote
                    curIndex = nextQuote;
                    if (curIndex + 1 < line.length()) {
                        // if there is a next value, set current index to be after delimiter
                        if (line.charAt(curIndex + 1) == cDelimiter) {
                            curIndex += 2;
                            // handle case where last value is empty
                            if (curIndex == line.length())
                                result.add("");
                        }
                        // else character after ending quote is not EOL and not delimiter, stop parsing
                        else
                            throw new IOException("Line " + lineNumber + ": expected a delimiter after the quote");
                    }
                    else
                        // end of line is after ending quote, stop parsing
                        curIndex++;
                }
            }
            else {
                // handle unquoted value
                nextDelimiter = getNextDelimiter(line, cDelimiter, curIndex);
                value = line.substring(curIndex, nextDelimiter).replace(doubleQuotes, singleQuotes);
                // unquoted values should not contain any quotes
                if (value.contains(singleQuotes))
                    throw new IOException("Line " + lineNumber + ": value contains some quotes but does not start with a quote");
                else {
                    result.add(value);
                    curIndex = nextDelimiter + 1;
                    // handle case where last value is empty
                    if (curIndex == line.length())
                        result.add("");
                }
            }
        }

        return result;
    }

    private static int getNextSingleQuote(String line, char quote, int from) {
        if (from >= line.length())
            return -1;

        int index = from + 1;
        boolean found = false;
        while ((index < line.length()) && !found) {
            if (line.charAt(index) != quote)
                index++;
            else {
                if ((index + 1 == line.length()) || (line.charAt(index + 1) != quote))
                    found = true;
                else
                    index += 2;
            }

        }

        index = (index == line.length()) ? -1 : index;

        return index;
    }

    private static int getNextDelimiter(String line, char delimiter, int from) {
        if (from >= line.length())
            return line.length();

        int index = from;
        while ((index < line.length()) && (line.charAt(index) != delimiter))
            index++;

        return index;
    }

    public static class NccrDictionary {

        @JsonProperty("Dictionary_Elements")
        private List<NccrDictionaryElement> _elements;

        public List<NccrDictionaryElement> getElements() {
            return _elements;
        }

        public void setElements(List<NccrDictionaryElement> elements) {
            _elements = elements;
        }
    }

    public static class NccrDictionaryElement {

        @JsonProperty("Column_Name_at_Source")
        private String _columnNameAtSource;

        @JsonProperty("Column_DataType_at_Source")
        private NccrDictionaryDataType _columnDataTypeAtSource;

        @JsonProperty("Item_Name")
        private String _itemName;

        @JsonProperty("Item_Id")
        private String _itemId;

        @JsonProperty("Item_Number")
        private String _itemNumber;

        @JsonProperty("Internet_Link")
        private String _internetLink;

        @JsonProperty("Item_Description")
        private String _itemDescription;

        @JsonProperty("Rationale")
        private String _rationale;

        @JsonProperty("Permissible_Values")
        private List<NccrDictionaryElementValue> _permissibleValues;

        public String getColumnNameAtSource() {
            return _columnNameAtSource;
        }

        public void setColumnNameAtSource(String columnNameAtSource) {
            _columnNameAtSource = columnNameAtSource;
        }

        public NccrDictionaryDataType getColumnDataTypeAtSource() {
            return _columnDataTypeAtSource;
        }

        public void setColumnDataTypeAtSource(NccrDictionaryDataType columnDataTypeAtSource) {
            _columnDataTypeAtSource = columnDataTypeAtSource;
        }

        public String getItemName() {
            return _itemName;
        }

        public void setItemName(String itemName) {
            _itemName = itemName;
        }

        public String getItemId() {
            return _itemId;
        }

        public void setItemId(String itemId) {
            _itemId = itemId;
        }

        public String getItemNumber() {
            return _itemNumber;
        }

        public void setItemNumber(String itemNumber) {
            _itemNumber = itemNumber;
        }

        public String getInternetLink() {
            return _internetLink;
        }

        public void setInternetLink(String internetLink) {
            _internetLink = internetLink;
        }

        public String getItemDescription() {
            return _itemDescription;
        }

        public void setItemDescription(String itemDescription) {
            _itemDescription = itemDescription;
        }

        public String getRationale() {
            return _rationale;
        }

        public void setRationale(String rationale) {
            _rationale = rationale;
        }

        public List<NccrDictionaryElementValue> getPermissibleValues() {
            return _permissibleValues;
        }

        public void setPermissibleValues(List<NccrDictionaryElementValue> permissibleValues) {
            _permissibleValues = permissibleValues;
        }
    }

    public static class NccrDictionaryDataType {

        @JsonProperty("Field_Length")
        private Integer _fieldLength;

        @JsonProperty("Semantic_Type")
        private String _semanticType;

        @JsonProperty("Abbreviated_Source_Vocabulary")
        private String _abbreviatedSourceVocabulary;

        public Integer getFieldLength() {
            return _fieldLength;
        }

        public void setFieldLength(Integer fieldLength) {
            _fieldLength = fieldLength;
        }

        public String getSemanticType() {
            return _semanticType;
        }

        public void setSemanticType(String semanticType) {
            _semanticType = semanticType;
        }

        public String getAbbreviatedSourceVocabulary() {
            return _abbreviatedSourceVocabulary;
        }

        public void setAbbreviatedSourceVocabulary(String abbreviatedSourceVocabulary) {
            _abbreviatedSourceVocabulary = abbreviatedSourceVocabulary;
        }
    }

    public static class NccrDictionaryElementValue {

        @JsonProperty("Value")
        private String _value;

        @JsonProperty("Description")
        private String _description;

        public String getValue() {
            return _value;
        }

        public void setValue(String value) {
            _value = value;
        }

        public String getDescription() {
            return _description;
        }

        public void setDescription(String description) {
            _description = description;
        }
    }

}
