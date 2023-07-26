package com.imsweb.naaccr.api.client.entity;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Data items returned by the API.
 */
public final class NaaccrDataItem {

    @JsonProperty("ItemName")
    private String _itemName;

    @JsonProperty("ItemNumber")
    private Integer _itemNumber;

    @JsonProperty("ItemLength")
    private Integer _itemLength;

    @JsonProperty("ItemDataType")
    private String _itemDataType;

    @JsonProperty("XmlNaaccrId")
    private String _xmlNaaccrId;

    @JsonProperty("XmlParentId")
    private String _xmlParentId;

    @JsonProperty("RecordTypes")
    private String _recordTypes;

    @JsonProperty("Section")
    private String _section;

    @JsonProperty("SourceOfStandard")
    private String _sourceOfStandard;

    @JsonProperty("DateCreated")
    private Date _dateCreated;

    @JsonProperty("DateModified")
    private Date _dateModified;

    @JsonProperty("NpcrCollected")
    private String _npcrCollected;

    @JsonProperty("CocCollected")
    private String _cocCollected;

    @JsonProperty("SeerCollected")
    private String _seerCollected;

    @JsonProperty("CccrCollected")
    private String _cccrCollected;

    @JsonProperty("AlternateNames")
    List<String> _alternateNames;

    @JsonProperty("Format")
    private String _format;

    @JsonProperty("AllowableValues")
    private String _allowableValues;

    @JsonProperty("YearImplemented")
    private Integer _yearImplemented;

    @JsonProperty("VersionImplemented")
    private String _versionImplemented;

    @JsonProperty("YearRetired")
    private Integer _yearRetired;

    @JsonProperty("VersionRetired")
    private String _versionRetired;

    @JsonProperty("Description")
    private String _description;

    @JsonProperty("Rationale")
    private String _rationale;

    @JsonProperty("Clarification")
    private String _clarification;

    @JsonProperty("GeneralNotes")
    private String _generalNotes;

    @JsonProperty("CodeHeading")
    private String _codeHeading;

    @JsonProperty("CodeNote")
    private String _codeNote;

    @JsonProperty("AllowedCodes")
    private List<NaaccrAllowedCode> _allowedCodes;

    public String getItemName() {
        return _itemName;
    }

    public void setItemName(String itemName) {
        _itemName = itemName;
    }

    public Integer getItemNumber() {
        return _itemNumber;
    }

    public void setItemNumber(Integer itemNumber) {
        _itemNumber = itemNumber;
    }

    public Integer getItemLength() {
        return _itemLength;
    }

    public void setItemLength(Integer itemLength) {
        _itemLength = itemLength;
    }

    public String getItemDataType() {
        return _itemDataType;
    }

    public void setItemDataType(String itemDataType) {
        _itemDataType = itemDataType;
    }

    public String getXmlNaaccrId() {
        return _xmlNaaccrId;
    }

    public void setXmlNaaccrId(String xmlNaaccrId) {
        _xmlNaaccrId = xmlNaaccrId;
    }

    public String getXmlParentId() {
        return _xmlParentId;
    }

    public void setXmlParentId(String xmlParentId) {
        _xmlParentId = xmlParentId;
    }

    public String getRecordTypes() {
        return _recordTypes;
    }

    public void setRecordTypes(String recordTypes) {
        _recordTypes = recordTypes;
    }

    public String getSection() {
        return _section;
    }

    public void setSection(String section) {
        _section = section;
    }

    public String getSourceOfStandard() {
        return _sourceOfStandard;
    }

    public void setSourceOfStandard(String sourceOfStandard) {
        _sourceOfStandard = sourceOfStandard;
    }

    public Date getDateCreated() {
        return _dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        _dateCreated = dateCreated;
    }

    public Date getDateModified() {
        return _dateModified;
    }

    public void setDateModified(Date dateModified) {
        _dateModified = dateModified;
    }

    public String getNpcrCollected() {
        return _npcrCollected;
    }

    public void setNpcrCollected(String npcrCollected) {
        _npcrCollected = npcrCollected;
    }

    public String getCocCollected() {
        return _cocCollected;
    }

    public void setCocCollected(String cocCollected) {
        _cocCollected = cocCollected;
    }

    public String getSeerCollected() {
        return _seerCollected;
    }

    public void setSeerCollected(String seerCollected) {
        _seerCollected = seerCollected;
    }

    public String getCccrCollected() {
        return _cccrCollected;
    }

    public void setCccrCollected(String cccrCollected) {
        _cccrCollected = cccrCollected;
    }

    public List<String> getAlternateNames() {
        return _alternateNames;
    }

    public void setAlternateNames(List<String> alternateNames) {
        _alternateNames = alternateNames;
    }

    public String getFormat() {
        return _format;
    }

    public void setFormat(String format) {
        _format = format;
    }

    public String getAllowableValues() {
        return _allowableValues;
    }

    public void setAllowableValues(String allowableValues) {
        _allowableValues = allowableValues;
    }

    public Integer getYearImplemented() {
        return _yearImplemented;
    }

    public void setYearImplemented(Integer yearImplemented) {
        _yearImplemented = yearImplemented;
    }

    public String getVersionImplemented() {
        return _versionImplemented;
    }

    public void setVersionImplemented(String versionImplemented) {
        _versionImplemented = versionImplemented;
    }

    public Integer getYearRetired() {
        return _yearRetired;
    }

    public void setYearRetired(Integer yearRetired) {
        _yearRetired = yearRetired;
    }

    public String getVersionRetired() {
        return _versionRetired;
    }

    public void setVersionRetired(String versionRetired) {
        _versionRetired = versionRetired;
    }

    public String getDescription() {
        return _description;
    }

    public void setDescription(String description) {
        _description = description;
    }

    public String getRationale() {
        return _rationale;
    }

    public void setRationale(String rationale) {
        _rationale = rationale;
    }

    public String getClarification() {
        return _clarification;
    }

    public void setClarification(String clarification) {
        _clarification = clarification;
    }

    public String getGeneralNotes() {
        return _generalNotes;
    }

    public void setGeneralNotes(String generalNotes) {
        _generalNotes = generalNotes;
    }

    public String getCodeHeading() {
        return _codeHeading;
    }

    public void setCodeHeading(String codeHeading) {
        _codeHeading = codeHeading;
    }

    public String getCodeNote() {
        return _codeNote;
    }

    public void setCodeNote(String codeNote) {
        _codeNote = codeNote;
    }

    public List<NaaccrAllowedCode> getAllowedCodes() {
        return _allowedCodes;
    }

    public void setAllowedCodes(List<NaaccrAllowedCode> allowedCodes) {
        _allowedCodes = allowedCodes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        NaaccrDataItem that = (NaaccrDataItem)o;
        return Objects.equals(_itemNumber, that._itemNumber) && Objects.equals(_xmlNaaccrId, that._xmlNaaccrId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_itemNumber, _xmlNaaccrId);
    }
}