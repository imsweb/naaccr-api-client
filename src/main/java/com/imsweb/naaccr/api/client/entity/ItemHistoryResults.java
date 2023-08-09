/*
 * Copyright (C) 2023 Information Management Services, Inc.
 */
package com.imsweb.naaccr.api.client.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ItemHistoryResults {

    @JsonProperty("count")
    private Integer _count;

    @JsonProperty("results")
    private List<ItemHistory> _results;

    public Integer getCount() {
        return _count;
    }

    public void setCount(Integer count) {
        _count = count;
    }

    public List<ItemHistory> getResults() {
        return _results;
    }

    public void setResults(List<ItemHistory> results) {
        _results = results;
    }
}
