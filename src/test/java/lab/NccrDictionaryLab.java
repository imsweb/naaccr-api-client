/*
 * Copyright (C) 2023 Information Management Services, Inc.
 */
package lab;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.imsweb.naaccr.api.client.NaaccrApiClient;
import com.imsweb.naaccr.api.client.entity.NaaccrAllowedCode;
import com.imsweb.naaccr.api.client.entity.NaaccrDataItem;

public class NccrDictionaryLab {

    public static void main(String[] args) throws IOException {
        NaaccrApiClient client = NaaccrApiClient.getInstance();

        String version = "23";
        List<String> itemsToFetch = Arrays.asList("ageAtDiagnosis", "ajccId", "behaviorIcdO2", "behaviorCodeIcdO3", "brainMolecularMarkers", "breslowTumorThickness", "cocAccreditedFlag",
                "csExtension", "csLymphNodes", "csLymphNodesEval", "csMetsAtDx", "csMetsEval");


        // the notes are provided by them, I didn't want to lose them, so I am "re-injecting" them
        Map<String, String> notes = new HashMap<>();
        notes.put("ageAtDiagnosis", "This data item will be used to create an efficient process for running TNM Edits. Each Site-Specific Data Item (SSDI) applies only to selected primary sites,histologies, and years of diagnosis. Depending on applicability and standard-setter requirements, SSDIs may be left blank.");
        notes.put("ajccId", "This data item will be used to create an efficient process for running TNM Edits.Each Site-Specific Data Item (SSDI) applies only to selected primary sites histologies, and years of diagnosis. Depending on applicability and standard-setter requirements, SSDIs may beleft blank.");
        notes.put("brainMolecularMarkers", "Collection of these clinically important brain cancer subtypes has been recommended by CBTRUS.");
        notes.put("breslowTumorThickness", "Breslow Tumor Thickness is a Registry Data Collection Variable in AJCC. It was previously collected as Melanoma Skin, CS SSF# 1.");
        notes.put("cocAccreditedFlag", "CoC-accredited facilities are required to collect certain data items including TNM staging. It is burdensome for central registries to maintain a list of accredited facilities, and the list changes frequently. The flag is a means of incorporating the accredited status into abstracts at the time of abstraction by someone who has knowledge of the status. The flag thus simplifies validating that required items have been abstracted by CoC-accredited facilities. The flag also allows cases to be stratified during analyses to identify those never seen at a CoC-accredited facility; e.g., percentage of all cases seen in at least one CoC-accredited facility, evaluation of outcomes by facility status. NPCR will use this flag for facility status stratification.");
        notes.put("csExtension", "Tumor extension at diagnosis is a prognostic indicator used by Collaborative Staging to derive some TNM-T codes and some SEER Summary Stage codes.");
        notes.put("csLymphNodes", "The involvement of specific regional lymph nodes is a prognostic indicator used by Collaborative Staging to derive some TNM-N codes and SEER Summary Stage codes.");
        notes.put("csLymphNodesEval", "This data item is used by Collaborative Staging to describe whether the staging basis for the TNM-N code is clinical or pathological and to record applicable prefix and suffix descriptors used with TNM staging.");
        notes.put("csMetsAtDx", "The presence of metastatic disease at diagnosis is an independent prognostic indicator, and it is used by Collaborative Staging to derive TNM-M codes and SEER Summary Stage codes.");
        notes.put("csMetsEval", "This data item is used by Collaborative Staging to describe whether the staging basis for the TNM-M code is clinical or pathological and to record applicable prefix and suffix descriptors used with TNM staging.");

        NccrDictionary dictionary = new NccrDictionary();
        dictionary.setNaaccrVersion(version + "0");
        dictionary.setElements(new ArrayList<>());

        for (String itemId : itemsToFetch) {
            NaaccrDataItem item = client.getDataItem(version, itemId);

            NccrDictionaryElement element = new NccrDictionaryElement();
            element.setItemName(item.getItemName());
            element.setItemId(item.getXmlNaaccrId());
            element.setItemNumber(item.getItemNumber().toString());
            element.setNaaccrLink("https://apps.naaccr.org/data-dictionary/api/1.0/data_item/" + version + "/" + item.getXmlNaaccrId());
            element.setItemDescription(item.getDescription());
            element.setRationale(notes.get(itemId));

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

            dictionary.getElements().add(element);
        }

        System.out.println(getMapper().writeValueAsString(dictionary));
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

    public static class NccrDictionary {

        @JsonProperty("NAACCR_Version")
        private String _naaccrVersion;

        @JsonProperty("Dictionary_Elements")
        private List<NccrDictionaryElement> _elements;

        public String getNaaccrVersion() {
            return _naaccrVersion;
        }

        public void setNaaccrVersion(String naaccrVersion) {
            _naaccrVersion = naaccrVersion;
        }

        public List<NccrDictionaryElement> getElements() {
            return _elements;
        }

        public void setElements(List<NccrDictionaryElement> elements) {
            _elements = elements;
        }
    }

    public static class NccrDictionaryElement {

        @JsonProperty("Item_Name")
        private String _itemName;

        @JsonProperty("Item_Id")
        private String _itemId;

        @JsonProperty("Item_Number")
        private String _itemNumber;

        @JsonProperty("NAACCR_Link")
        private String _naaccrLink;

        @JsonProperty("Item_Description")
        private String _itemDescription;

        @JsonProperty("Rationale")
        private String _rationale;

        @JsonProperty("Permissible_Values")
        private List<NccrDictionaryElementValue> _permissibleValues;

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

        public String getNaaccrLink() {
            return _naaccrLink;
        }

        public void setNaaccrLink(String naaccrLink) {
            _naaccrLink = naaccrLink;
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
