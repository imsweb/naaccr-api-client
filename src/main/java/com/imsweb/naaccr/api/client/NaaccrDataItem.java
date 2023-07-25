package com.imsweb.naaccr.api.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class NaaccrDataItem {

    @JsonProperty("ItemName")
    private String itemName;

    @JsonProperty("ItemNumber")
    private Integer itemNumber;

    @JsonProperty("ItemLength")
    private Integer itemLength;

    @JsonProperty("XmlNaaccrId")
    private String xmlNaaccrId;

    @JsonProperty("XmlParentId")
    private String xmlParentId;

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public Integer getItemNumber() {
        return itemNumber;
    }

    public void setItemNumber(Integer itemNumber) {
        this.itemNumber = itemNumber;
    }

    public Integer getItemLength() {
        return itemLength;
    }

    public void setItemLength(Integer itemLength) {
        this.itemLength = itemLength;
    }

    public String getXmlNaaccrId() {
        return xmlNaaccrId;
    }

    public void setXmlNaaccrId(String xmlNaaccrId) {
        this.xmlNaaccrId = xmlNaaccrId;
    }

    public String getXmlParentId() {
        return xmlParentId;
    }

    public void setXmlParentId(String xmlParentId) {
        this.xmlParentId = xmlParentId;
    }
}