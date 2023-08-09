/*
 * Copyright (C) 2023 Information Management Services, Inc.
 */
package com.imsweb.naaccr.api.client;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

import com.imsweb.naaccr.api.client.entity.ItemHistoryResults;
import com.imsweb.naaccr.api.client.entity.NaaccrDataItem;
import com.imsweb.naaccr.api.client.entity.NaaccrVersion;
import com.imsweb.naaccr.api.client.entity.SearchResults;

public interface NaaccrApiService {

    @GET("naaccr_versions")
    Call<List<NaaccrVersion>> getNaaccrVersions();

    @GET("data_item/{naaccrVersion}/{xmlId}")
    Call<NaaccrDataItem> getDataItem(@Path("naaccrVersion") String naaccrVersion, @Path("xmlId") String xmlId);

    @GET("data_item/{naaccrVersion}")
    Call<SearchResults> getDataItems(@Path("naaccrVersion") String naaccrVersion, @Query("q") String search, @Query("page") Integer page);

    // TODO add same call but by number

    @GET("data_item/{xmlId}/history")
    Call<ItemHistoryResults> getItemHistory(@Path("xmlId") String xmlId, @Query("attribute") String attribute);

}
