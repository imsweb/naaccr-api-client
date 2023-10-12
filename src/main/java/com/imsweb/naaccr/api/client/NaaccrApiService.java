/*
 * Copyright (C) 2023 Information Management Services, Inc.
 */
package com.imsweb.naaccr.api.client;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

import com.imsweb.naaccr.api.client.entity.ItemChangelogResults;
import com.imsweb.naaccr.api.client.entity.ItemHistoryResults;
import com.imsweb.naaccr.api.client.entity.NaaccrDataItem;
import com.imsweb.naaccr.api.client.entity.NaaccrVersion;
import com.imsweb.naaccr.api.client.entity.SearchResults;

public interface NaaccrApiService {

    @GET("naaccr_versions")
    Call<List<NaaccrVersion>> getNaaccrVersions();

    @GET("data_item/{naaccrVersion}")
    Call<SearchResults> getDataItems(@Path("naaccrVersion") String naaccrVersion, @Query("page") Integer page);

    @GET("data_item/{naaccrVersion}/{xmlIdOrNumber}")
    Call<NaaccrDataItem> getDataItem(@Path("naaccrVersion") String naaccrVersion, @Path("xmlIdOrNumber") String xmlIdOrNumber);

    @GET("data_item/operation_history/{naaccrVersion}/{xmlIdOrNumber}")
    Call<ItemChangelogResults> getItemChangelog(@Path("naaccrVersion") String naaccrVersion, @Path("xmlIdOrNumber") String xmlIdOrNumber);

    @GET("data_item/{xmlIdOrNumber}/history")
    Call<ItemHistoryResults> getItemHistory(@Path("xmlIdOrNumber") String xmlIdOrNumber, @Query("attribute") String attribute);

}
