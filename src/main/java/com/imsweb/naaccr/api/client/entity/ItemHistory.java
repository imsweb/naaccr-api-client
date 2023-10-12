/*
 * Copyright (C) 2023 Information Management Services, Inc.
 */
package com.imsweb.naaccr.api.client.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

@SuppressWarnings("unused")
public class ItemHistory {

    @JsonProperty("NaaccrVersion")
    private String _naaccrVersion;

    @JsonProperty("Value")
    private String _value;

    public String getNaaccrVersion() {
        return _naaccrVersion;
    }

    public void setNaaccrVersion(String naaccrVersion) {
        _naaccrVersion = naaccrVersion;
    }

    public String getValue() {
        return _value;
    }

    public void setValue(String value) {
        _value = value;
    }
}
