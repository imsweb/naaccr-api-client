/*
 * Copyright (C) 2023 Information Management Services, Inc.
 */
package com.imsweb.naaccr.api.client.entity;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

public class NaaccrVersion {

    @JsonProperty("Version")
    private String _version;

    @JsonProperty("YearImplemented")
    private Integer _yearImplemented;

    // TODO FD add date of publication, but it uses a different format, need to discuss this ("DateOfPublication": "2023-04-05 13:09:58.558000")

    public String getVersion() {
        return _version;
    }

    public void setVersion(String version) {
        _version = version;
    }

    public Integer getYearImplemented() {
        return _yearImplemented;
    }

    public void setYearImplemented(Integer yearImplemented) {
        _yearImplemented = yearImplemented;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        NaaccrVersion that = (NaaccrVersion)o;
        return Objects.equals(_version, that._version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_version);
    }
}
