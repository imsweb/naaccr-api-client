/*
 * Copyright (C) 2023 Information Management Services, Inc.
 */
package com.imsweb.naaccr.api.client;

import java.io.IOException;

import org.jetbrains.annotations.NotNull;

import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.Interceptor;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Interceptor to catch all non-200 responses and convert them to exceptions.
 */
public class ErrorInterceptor implements Interceptor {

    @NotNull
    @Override
    public Response intercept(Chain chain) throws IOException {
        Response response = chain.proceed(chain.request());

        if (response.code() != 200) {
            String message;

            ResponseBody body = response.body();
            if (body != null) {
                try {
                    message = new ObjectMapper().readValue(body.byteStream(), ErrorResponse.class).getMessage();
                }
                catch (IOException e) {
                    message = body.string();
                }
            }
            else
                message = "Error code " + response.code();

            throw new NaaccrApiException(response.code(), message);
        }

        return response;
    }
}