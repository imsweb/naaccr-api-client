/*
 * Copyright (C) 2023 Information Management Services, Inc.
 */
package com.imsweb.naaccr.api.client;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface NaaccrApiService {

    /**
     * TODO FD
     */
    @GET("data_item/{naaccrVersion}/{xmlId}")
    Call<NaaccrDataItem> getDataItem(@Path("naaccrVersion") String naaccrVersion, @Path("xmlId") String xmlId);
}
