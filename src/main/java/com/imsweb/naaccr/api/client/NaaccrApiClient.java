/*
 * Copyright (C) 2023 Information Management Services, Inc.
 */
package com.imsweb.naaccr.api.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Properties;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class NaaccrApiClient {

    private final NaaccrApiService service;

    /**
     * Creates a client API root object
     * @param baseUrl base URL for API
     */
    private NaaccrApiClient(String baseUrl, String apiVersion) {
        if (!baseUrl.endsWith("/"))
            baseUrl += "/";

        baseUrl += apiVersion + "/";

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request original = chain.request();

                    // add the api key to all requests
                    Request request = original.newBuilder()
                            .header("Accept", "application/json")
                            .method(original.method(), original.body())
                            .build();

                    return chain.proceed(request);
                })
                //.addInterceptor(new ErrorInterceptor())  TODO FD
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(JacksonConverterFactory.create(getMapper()))
                .client(client)
                .build();

        // create cached service entities
        service = retrofit.create(NaaccrApiService.class);
    }

    /**
     * Returns the internal object mapper.
     * @return the internal object mapper
     */
    static ObjectMapper getMapper() {
        ObjectMapper mapper = new ObjectMapper();

        // do not write null values
        mapper.setSerializationInclusion(Include.NON_NULL);

        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

        // set Date objects to output in readable customized format
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        mapper.setDateFormat(dateFormat);

        return mapper;
    }

    public NaaccrApiService getService() {
        return service;
    }


    public static class Builder {

        // default base URL
        private static final String _NAACCR_API_URL = "https://apps.naaccr.org/data-dictionary/api/";

        private static final String _NAACCR_API_VERSION = "1.0";

        private String url;

        private String version;

        /**
         * Return a list of user properties from the local .naaccr-api-client file
         * @return Properties object
         */
        private Properties getProperties() {
            Properties props = new Properties();

            File config = new File(System.getProperty("user.home"), ".naaccr-api-client");
            if (config.exists()) {

                try (FileInputStream in = new FileInputStream(config)) {
                    props.load(in);
                }
                catch (IOException e) {
                    // error reading
                }
            }

            return props;
        }

        /**
         * Constructor defaults url using the key stored in ~/.seer-api-client or the environment variable NAACCR_API_CLIENT_URL
         */
        public Builder() {
            Properties props = getProperties();

            // if the URL is specified (either in properties file or environment), use it, otherwise use the default
            url = props.getProperty("url");
            if (url == null)
                url = System.getenv("NAACCR_API_CLIENT_URL");
            if (url == null)
                url = _NAACCR_API_URL;

            version = props.getProperty("version");
            if (version == null)
                version = System.getenv("NAACCR_API_CLIENT_VERSION");
            if (version == null)
                version = _NAACCR_API_VERSION;
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder version(String version) {
            this.version = version;
            return this;
        }

        public NaaccrApiClient connect() {
            return new NaaccrApiClient(url, version);
        }
    }

}
