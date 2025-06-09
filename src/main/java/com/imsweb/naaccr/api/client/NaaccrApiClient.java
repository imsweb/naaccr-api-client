/*
 * Copyright (C) 2023 Information Management Services, Inc.
 */
package com.imsweb.naaccr.api.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
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

import com.imsweb.naaccr.api.client.entity.ItemChangelog;
import com.imsweb.naaccr.api.client.entity.ItemChangelogResults;
import com.imsweb.naaccr.api.client.entity.ItemHistory;
import com.imsweb.naaccr.api.client.entity.ItemHistoryResults;
import com.imsweb.naaccr.api.client.entity.NaaccrDataItem;
import com.imsweb.naaccr.api.client.entity.NaaccrVersion;
import com.imsweb.naaccr.api.client.entity.SearchResults;

public final class NaaccrApiClient {

    // default base URL
    public static final String NAACCR_API_URL = "https://apps.naaccr.org/data-dictionary/api/";

    // default API version
    public static final String NAACCR_API_VERSION = "1.0";

    // constants for the NAACCR versions
    public static final String NAACCR_25 = "25";
    public static final String NAACCR_24 = "24";
    public static final String NAACCR_23 = "23";
    public static final String NAACCR_22 = "22";
    public static final String NAACCR_21 = "21";

    // latest NAACCR version
    public static final String NAACCR_LATEST = NAACCR_25;

    public enum NaaccrItemAttribute {
        ITEM_NAME("ItemName"),
        ITEM_NUMBER("ItemNumber"),
        ITEM_LENGTH("ItemLength"),
        ITEM_DATA_TYPE("ItemDataType"),
        XML_NAACCR_ID("XmlNaaccrId"),
        XML_PARENT_ID("XmlParentId"),
        RECORD_TYPES("RecordTypes"),
        ALLOWABLE_VALUES("AllowableValues"),
        YEAR_IMPLEMENTED("YearImplemented"),
        VERSION_IMPLEMENTED("VersionImplemented"),
        YEAR_RETIRED("YearRetired"),
        VERSION_RETIRED("VersionRetired"),
        SECTION("Section"),
        SOURCE_OF_STANDARD("SourceOfStandard"),
        DESCRIPTION("Description"),
        RATIONALE("Rationale"),
        CLARIFICATION("Clarification"),
        GENERAL_NOTES("GeneralNotes"),
        COLLECTION_STATUS_NPCR("NpcrCollect"),
        COLLECTION_STATUS_COC("CocCollect"),
        COLLECTION_STATUS_SEER("SeerCollect"),
        FORMAT("Format"),
        CODE_HEADING("CodeHeading"),
        CODE_NOTE("CodeNote"),
        ALTERNATE_NAMES("AlternateNames"),
        ALLOWED_CODES("AllowedCodes");

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

        return mapper;
    }

    private final NaaccrApiService _service;

    /**
     * Creates a client API root object
     * @param baseUrl base URL for API
     */
    private NaaccrApiClient(String baseUrl, String apiVersion, String authorization) {

        if (!baseUrl.endsWith("/"))
            baseUrl += "/";
        baseUrl += apiVersion + "/";

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request original = chain.request();
                    Request.Builder requestBuilder = original.newBuilder();
                    requestBuilder.addHeader("Accept", "application/json");
                    if (authorization != null)
                        requestBuilder.addHeader("Authorization", authorization);
                    requestBuilder.method(original.method(), original.body());
                    return chain.proceed(requestBuilder.build());
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

    public List<NaaccrDataItem> getDataItems(String naaccrVersion) throws IOException {
        List<NaaccrDataItem> items = new ArrayList<>();

        SearchResults results = _service.getDataItems(naaccrVersion, null).execute().body();
        if (results == null)
            throw new IOException("Got no results");
        if (results.getResults() != null && !results.getResults().isEmpty())
            items.addAll(results.getResults());
        if (results.getCount() == null)
            throw new IOException("Was expecting a count in the results but didn't get it");
        int count = results.getCount();
        int page = 2;
        while (items.size() < count) {
            results = _service.getDataItems(naaccrVersion, page++).execute().body();
            if (results == null || results.getResults() == null || results.getResults().isEmpty())
                break;
            items.addAll(results.getResults());
        }

        return items;
    }

    /**
     * Returns the requested data item.
     * @param naaccrVersion NAACCR version
     * @param xmlId data item XML ID
     * @return the requested data item, never null
     * @throws IOException if the call could not complete successfully or the item was not found
     */
    public NaaccrDataItem getDataItem(String naaccrVersion, String xmlId) throws IOException {
        return _service.getDataItem(naaccrVersion, xmlId).execute().body();
    }

    /**
     * Returns the requested data item.
     * @param naaccrVersion NAACCR version
     * @param itemNumber data item number
     * @return the requested data item, never null
     * @throws IOException if the call could not complete successfully or the item was not found
     */
    public NaaccrDataItem getDataItem(String naaccrVersion, Integer itemNumber) throws IOException {
        return getDataItem(naaccrVersion, itemNumber == null ? "" : itemNumber.toString());
    }

    /**
     * Returns the changelog within a given NAACCR version for all the attributes of a given item.
     * @param naaccrVersion NAACCR version
     * @param xmlId data item XML ID
     * @return the list of changelog entries, never null
     * @throws IOException if the call could not complete successfully
     */
    public List<ItemChangelog> getItemChangelog(String naaccrVersion, String xmlId) throws IOException {
        ItemChangelogResults results = _service.getItemChangelog(naaccrVersion, xmlId).execute().body();
        if (results == null)
            throw new IOException("Unable to get changelog, got null results");
        return results.getResults().stream().sorted(Comparator.comparing(ItemChangelog::getModifiedAttribute)).collect(Collectors.toList());
    }

    /**
     * Returns the changelog within a given NAACCR version for all the attributes of a given item.
     * @param naaccrVersion NAACCR version
     * @param itemNumber data item number
     * @return the list of changelog entries, never null
     * @throws IOException if the call could not complete successfully
     */
    public List<ItemChangelog> getItemChangelog(String naaccrVersion, Integer itemNumber) throws IOException {
        return getItemChangelog(naaccrVersion, itemNumber == null ? "" : itemNumber.toString());
    }

    /**
     * Returns the history across NAACCR versions for the requested attribute and item.
     * @param xmlId data item XML ID
     * @param attribute item attribute (name, length, etc...)
     * @return the list of history entries, never null
     * @throws IOException if the call could not complete successfully
     */
    public List<ItemHistory> getItemHistory(String xmlId, NaaccrItemAttribute attribute) throws IOException {
        ItemHistoryResults results = _service.getItemHistory(xmlId, attribute.getName()).execute().body();
        if (results == null)
            throw new IOException("Unable to get history, got null results");
        return results.getResults().stream().sorted(Comparator.comparing(ItemHistory::getNaaccrVersion)).collect(Collectors.toList());
    }

    /**
     * Returns the history across NAACCR versions for the requested attribute and item.
     * @param itemNumber data item number
     * @param attribute item attribute (name, length, etc...)
     * @return the list of history entries, never null
     * @throws IOException if the call could not complete successfully
     */
    public List<ItemHistory> getItemHistory(Integer itemNumber, NaaccrItemAttribute attribute) throws IOException {
        return getItemHistory(itemNumber == null ? "" : itemNumber.toString(), attribute);
    }

    /**
     * Use this class to build a new NAACCR API client object.
     */
    public static class Builder {

        private String _url;

        private String _version;

        private String _authorization;

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

            _authorization = props.getProperty("authorization");
            if (_authorization == null)
                _authorization = System.getenv("NAACCR_API_CLIENT_AUTHORIZATION");
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
            return new NaaccrApiClient(_url, _version, _authorization);
        }
    }

}
