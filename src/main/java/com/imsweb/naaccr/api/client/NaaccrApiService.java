/*
 * Copyright (C) 2023 Information Management Services, Inc.
 */
package com.imsweb.naaccr.api.client;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

import com.imsweb.naaccr.api.client.entity.NaaccrDataItem;
import com.imsweb.naaccr.api.client.entity.SearchResults;

public interface NaaccrApiService {

    @GET("data_item/{naaccrVersion}/{xmlId}")
    Call<NaaccrDataItem> getDataItem(@Path("naaccrVersion") String naaccrVersion, @Path("xmlId") String xmlId);

    @GET("data_item/{naaccrVersion}")
    Call<SearchResults> getDataItems(@Path("naaccrVersion") String naaccrVersion, @Query("q") String search, @Query("page") Integer page);
}
