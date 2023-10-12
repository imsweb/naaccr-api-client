/*
 * Copyright (C) 2023 Information Management Services, Inc.
 */
package com.imsweb.naaccr.api.client.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

@SuppressWarnings("unused")
public class ItemChangelog {

    @JsonProperty("Operation")
    private String _operation;

    @JsonProperty("ModifiedAttribute")
    private String _modifiedAttribute;

    @JsonProperty("OldValue")
    private Object _oldValue;

    @JsonProperty("NewValue")
    private Object _newValue;

    public String getOperation() {
        return _operation;
    }

    public void setOperation(String operation) {
        _operation = operation;
    }

    public String getModifiedAttribute() {
        return _modifiedAttribute;
    }

    public void setModifiedAttribute(String modifiedAttribute) {
        _modifiedAttribute = modifiedAttribute;
    }

    public Object getOldValue() {
        return _oldValue;
    }

    public void setOldValue(String oldValue) {
        _oldValue = oldValue;
    }

    public Object getNewValue() {
        return _newValue;
    }

    public void setNewValue(String newValue) {
        _newValue = newValue;
    }
}
