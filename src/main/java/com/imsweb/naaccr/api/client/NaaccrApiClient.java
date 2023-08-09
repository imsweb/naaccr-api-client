/*
 * Copyright (C) 2023 Information Management Services, Inc.
 */
package com.imsweb.naaccr.api.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import com.imsweb.naaccr.api.client.entity.ItemHistory;
import com.imsweb.naaccr.api.client.entity.ItemHistoryResults;
import com.imsweb.naaccr.api.client.entity.NaaccrDataItem;
import com.imsweb.naaccr.api.client.entity.NaaccrVersion;
import com.imsweb.naaccr.api.client.entity.SearchResults;

public class NaaccrApiClient {

    // default base URL
    public static final String NAACCR_API_URL = "https://apps.naaccr.org/data-dictionary/api/";

    // default API version
    public static final String NAACCR_API_VERSION = "1.0";

    // constants for the NAACCR versions
    public static final String NAACCR_24 = "24";
    public static final String NAACCR_23 = "23";
    public static final String NAACCR_22 = "22";
    public static final String NAACCR_21 = "21";

    // list of all the supported NAACCR versions
    public static final List<String> NAACCR_VERSIONS = Arrays.asList(NAACCR_24, NAACCR_23, NAACCR_22, NAACCR_21);

    public enum NaaccrItemAttribute {
        ITEM_NAME("ItemName"),
        ITEM_LENGTH("ItemLength");
        // TODO FD add other attributes

        //        ItemName
        //                ItemLength
        //        ImplementedYear
        //                ImplementedVersion
        //        RetiredYear
        //                Section
        //        SourceOfStandard
        //                DateCreated
        //        DateModified
        //                Description
        //        Rationale
        //                Clarification
        //        GeneralNotes
        //                CollectionStatusNpcr
        //        CollectionStatusCoc
        //                CollectionStatusSeer
        //        CollectionStatusCccr
        //                Format
        //        CodeHeading
        //                CodeNote
        //        ItemDataType
        //                AllowableValues

        private final String _name;

        NaaccrItemAttribute(String name) {
            _name = name;
        }

        public String getName() {
            return _name;
        }
    }

    // cached instance
    private static NaaccrApiClient _INSTANCE;

    /**
     * Returns an instance of the client.
     * @return a NAACCR API client
     */
    public static NaaccrApiClient getInstance() {
        if (_INSTANCE == null)
            _INSTANCE = new Builder().connect();
        return _INSTANCE;
    }

    /**
     * Returns the internal object mapper.
     * @return the internal object mapper
     */
    static ObjectMapper getMapper() {
        ObjectMapper mapper = new ObjectMapper();

        // do not fail for unknown fields
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        // do not write null values
        mapper.setSerializationInclusion(Include.NON_NULL);

        // annotations are set on the fields (not the getters/setters)
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

        // set date format
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

        return mapper;
    }

    private final NaaccrApiService _service;

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
                .addInterceptor(new ErrorInterceptor())
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(JacksonConverterFactory.create(getMapper()))
                .client(client)
                .build();

        // create cached service entities
        _service = retrofit.create(NaaccrApiService.class);
    }

    public List<NaaccrVersion> getNaaccrVersions() throws IOException {
        List<NaaccrVersion> results = _service.getNaaccrVersions().execute().body();
        if (results == null)
            throw new IOException("Unable to get versions, got null results");
        return results.stream().sorted(Comparator.comparing(NaaccrVersion::getVersion)).collect(Collectors.toList());
    }

    /**
     * Returns the requested data item.
     * @param naaccrVersion NAACCR version
     * @param xmlId data item XML ID
     * @return the requested data item
     * @throws IOException if the call could not complete successfully
     */
    public NaaccrDataItem getDataItem(String naaccrVersion, String xmlId) throws IOException {
        return _service.getDataItem(naaccrVersion, xmlId).execute().body();
    }

    public List<NaaccrDataItem> getDataItems(String naaccrVersion) throws IOException {
        List<NaaccrDataItem> items = new ArrayList<>();

        long start = System.currentTimeMillis();
        System.out.println("Call #1");
        SearchResults results = _service.getDataItems(naaccrVersion, null, null).execute().body();
        if (results == null)
            throw new IOException("Got no results");
        if (results.getResults() != null && !results.getResults().isEmpty()) {
            System.out.println(" > " + results.getResults().get(0).getItemName() + " (" + results.getResults().size() + " items in " + (System.currentTimeMillis() - start) + "ms)");
            items.addAll(results.getResults());
        }
        if (results.getCount() == null)
            throw new IOException("Was expecting a count in the results but didn't get it");
        int count = results.getCount();
        int page = 2;
        while (items.size() < count) {
            System.out.println("Call #" + (page));
            start = System.currentTimeMillis();
            results = _service.getDataItems(naaccrVersion, null, page++).execute().body();
            if (results == null || results.getResults() == null || results.getResults().isEmpty())
                break;
            System.out.println(" > " + results.getResults().get(0).getItemName() + " (" + results.getResults().size() + " items in " + (System.currentTimeMillis() - start) + "ms)");
            items.addAll(results.getResults());
        }

        return items;
    }

    //    public List<NaaccrDataItem> searchDataItems(String naaccrVersion, String search) throws IOException {
    //        return _service.getDataItems(naaccrVersion, search).execute().body();
    //    }

    public List<ItemHistory> getItemHistory(String xmlId, NaaccrItemAttribute attribute) throws IOException {
        ItemHistoryResults results = _service.getItemHistory(xmlId, attribute.getName()).execute().body();
        if (results == null)
            throw new IOException("Unable to get history, got null results");
        return results.getResults().stream().sorted(Comparator.comparing(ItemHistory::getNaaccrVersion)).collect(Collectors.toList());
    }

    public static class Builder {

        private String _url;

        private String _version;

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
            _url = props.getProperty("url");
            if (_url == null)
                _url = System.getenv("NAACCR_API_CLIENT_URL");
            if (_url == null)
                _url = NAACCR_API_URL;

            _version = props.getProperty("version");
            if (_version == null)
                _version = System.getenv("NAACCR_API_CLIENT_VERSION");
            if (_version == null)
                _version = NAACCR_API_VERSION;
        }

        public Builder url(String url) {
            _url = url;
            return this;
        }

        public Builder version(String version) {
            _version = version;
            return this;
        }

        public NaaccrApiClient connect() {
            return new NaaccrApiClient(_url, _version);
        }
    }

}
