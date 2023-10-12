/*
 * Copyright (C) 2023 Information Management Services, Inc.
 */
package com.imsweb.naaccr.api.client.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

@SuppressWarnings("unused")
public class SearchResults {

    @JsonProperty("count")
    private Integer _count;

    @JsonProperty("next")
    private String _next;

    @JsonProperty("results")
    private List<NaaccrDataItem> _results;

    public Integer getCount() {
        return _count;
    }

    public void setCount(Integer count) {
        _count = count;
    }

    public String getNext() {
        return _next;
    }

    public void setNext(String next) {
        _next = next;
    }

    public List<NaaccrDataItem> getResults() {
        return _results;
    }

    public void setResults(List<NaaccrDataItem> results) {
        _results = results;
    }
}
