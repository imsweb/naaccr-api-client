/*
 * Copyright (C) 2023 Information Management Services, Inc.
 */
package com.imsweb.naaccr.api.client.entity;

import java.util.Date;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

@SuppressWarnings("unused")
public class NaaccrVersion {

    @JsonProperty("Version")
    private String _version;

    @JsonProperty("YearImplemented")
    private Integer _yearImplemented;

    @JsonProperty("DateOfPublication")
    private Date _dateOfPublication;

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

    public Date getDateOfPublication() {
        return _dateOfPublication;
    }

    public void setDateOfPublication(Date dateOfPublication) {
        _dateOfPublication = dateOfPublication;
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
