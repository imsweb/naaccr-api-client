/*
 * Copyright (C) 2023 Information Management Services, Inc.
 */
package com.imsweb.naaccr.api.client.entity;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Allowed code for a given data item.
 */
public class NaaccrAllowedCode {

    @JsonProperty("Code")
    private String _code;

    @JsonProperty("Description")
    private String _description;

    public String getCode() {
        return _code;
    }

    public void setCode(String code) {
        _code = code;
    }

    public String getDescription() {
        return _description;
    }

    public void setDescription(String description) {
        _description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        NaaccrAllowedCode that = (NaaccrAllowedCode)o;
        return Objects.equals(_code, that._code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_code);
    }
}
