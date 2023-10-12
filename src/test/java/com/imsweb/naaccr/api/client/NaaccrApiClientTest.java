/*
 * Copyright (C) 2023 Information Management Services, Inc.
 */
package com.imsweb.naaccr.api.client;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;

import com.imsweb.naaccr.api.client.NaaccrApiClient.Builder;
import com.imsweb.naaccr.api.client.NaaccrApiClient.NaaccrItemAttribute;
import com.imsweb.naaccr.api.client.entity.ItemChangelog;
import com.imsweb.naaccr.api.client.entity.ItemHistory;
import com.imsweb.naaccr.api.client.entity.NaaccrAllowedCode;
import com.imsweb.naaccr.api.client.entity.NaaccrDataItem;
import com.imsweb.naaccr.api.client.entity.NaaccrVersion;

import static com.imsweb.naaccr.api.client.NaaccrApiClient.NAACCR_21;
import static com.imsweb.naaccr.api.client.NaaccrApiClient.NAACCR_22;
import static com.imsweb.naaccr.api.client.NaaccrApiClient.NAACCR_23;
import static com.imsweb.naaccr.api.client.NaaccrApiClient.NAACCR_24;
import static com.imsweb.naaccr.api.client.NaaccrApiClient.NAACCR_API_URL;
import static com.imsweb.naaccr.api.client.NaaccrApiClient.NAACCR_API_VERSION;
import static com.imsweb.naaccr.api.client.NaaccrApiClient.NAACCR_LATEST;

public class NaaccrApiClientTest {

    @Test
    public void testClientBuilder() throws IOException {
        Assert.assertFalse(new Builder().url(NAACCR_API_URL).version(NAACCR_API_VERSION).connect().getNaaccrVersions().isEmpty());
    }

    @Test
    public void testGetNaaccrVersions() throws IOException {
        NaaccrApiClient client = NaaccrApiClient.getInstance();

        Set<String> versions = client.getNaaccrVersions().stream().map(NaaccrVersion::getVersion).collect(Collectors.toSet());
        Assert.assertFalse(versions.isEmpty());
        Assert.assertTrue(versions.toString(), versions.contains("21"));
        for (String v : versions)
            Assert.assertTrue(v, v.matches("\\d{2}"));
    }

    @Test
    public void testGetDataItem() throws IOException {
        NaaccrApiClient client = NaaccrApiClient.getInstance();

        // normal item (with codes)
        NaaccrDataItem item = client.getDataItem(NAACCR_LATEST, "ageAtDiagnosis");
        Assert.assertEquals("Age at Diagnosis", item.getItemName());
        Assert.assertEquals(230, item.getItemNumber().intValue());
        Assert.assertEquals(3, item.getItemLength().intValue());
        Assert.assertEquals("ageAtDiagnosis", item.getXmlNaaccrId());
        Assert.assertEquals("Tumor", item.getXmlParentId());
        Assert.assertEquals("A,M,C,I", item.getRecordTypes());
        Assert.assertEquals("digits", item.getItemDataType());
        Assert.assertNotNull(item.getFormat());
        Assert.assertNotNull(item.getAllowableValues());
        Assert.assertEquals("Demographic", item.getSection());
        Assert.assertEquals("SEER/CoC", item.getSourceOfStandard());
        Assert.assertEquals("R", item.getSeerCollect());
        Assert.assertEquals("R", item.getCocCollect());
        Assert.assertEquals("R", item.getNpcrCollect());
        Assert.assertNotNull(item.getDescription());
        Assert.assertNull(item.getYearImplemented());
        Assert.assertNull(item.getVersionImplemented());
        Assert.assertNull(item.getYearRetired());
        Assert.assertNull(item.getVersionRetired());
        Assert.assertNotNull(item.getDateCreated());
        Assert.assertNotNull(item.getDateModified());
        Assert.assertNull(item.getAlternateNames());
        List<NaaccrAllowedCode> codes = item.getAllowedCodes();
        Assert.assertNotNull(codes);
        Assert.assertFalse(codes.isEmpty());
        Assert.assertEquals("000", codes.get(0).getCode());
        Assert.assertEquals("Less than 1 year old; diagnosed in utero", codes.get(0).getDescription());

        // a recently added item
        item = client.getDataItem(NAACCR_23, "noPatientContactFlag");
        Assert.assertEquals("No Patient Contact Flag", item.getItemName());
        Assert.assertEquals(1854, item.getItemNumber().intValue());
        Assert.assertEquals(1, item.getItemLength().intValue());
        Assert.assertEquals("noPatientContactFlag", item.getXmlNaaccrId());
        Assert.assertEquals(2023, item.getYearImplemented().intValue());
        Assert.assertEquals("23", item.getVersionImplemented());
        Assert.assertNull(item.getYearRetired());
        Assert.assertNull(item.getVersionRetired());

        // item with alternate names
        item = client.getDataItem(NAACCR_22, "reportingFacility");
        Assert.assertEquals("Reporting Facility", item.getItemName());
        List<String> alternateNames = item.getAlternateNames();
        Assert.assertNotNull(alternateNames);
        Assert.assertFalse(alternateNames.isEmpty());
        Assert.assertTrue(alternateNames.contains("Facility Identification Number (CoC)"));
        Assert.assertTrue(alternateNames.contains("Institution ID Number (CoC)"));
        Assert.assertTrue(alternateNames.contains("Reporting Hospital"));

        // retired items (apparently they can only be fetched by number, which doesn't make sense to me)
        item = client.getDataItem(NAACCR_24, 2390);
        Assert.assertEquals("Name--Maiden", item.getItemName());
        Assert.assertEquals(2390, item.getItemNumber().intValue());
        Assert.assertNull(item.getItemLength());
        Assert.assertNull(item.getXmlNaaccrId());
        Assert.assertNull(item.getYearImplemented());
        Assert.assertNull(item.getVersionImplemented());
        Assert.assertEquals(2024, item.getYearRetired().intValue());
        Assert.assertEquals("24", item.getVersionRetired());

        // another retired item
        item = client.getDataItem(NAACCR_21, 1030);
        Assert.assertEquals("TNM Other Stage Group", item.getItemName());
        Assert.assertEquals(1030, item.getItemNumber().intValue());
        Assert.assertNull(item.getItemLength());
        Assert.assertNull(item.getXmlNaaccrId());
        Assert.assertNull(item.getYearImplemented());
        Assert.assertNull(item.getVersionImplemented());
        Assert.assertEquals(2006, item.getYearRetired().intValue());
        Assert.assertEquals("11", item.getVersionRetired());

        // a bad request
        Assert.assertThrows(IOException.class, () -> client.getDataItem(NAACCR_LATEST, "unknown"));
    }

    @Test
    public void testGetDataItems() throws IOException {
        NaaccrApiClient client = NaaccrApiClient.getInstance();

        List<NaaccrDataItem> items = client.getDataItems(NAACCR_LATEST);
        Assert.assertTrue(items.size() > 750);
        for (NaaccrDataItem item : items)
            Assert.assertNotNull(item.getItemName());
    }

    @Test
    public void testGetItemChangelog() throws IOException {
        NaaccrApiClient client = NaaccrApiClient.getInstance();

        // there is a big problem with the changelogs, they returned sometime a string and sometime an array (and the operation is always a modification);
        // that makes it impossible to properly deserializing, that's why the old/new values were mapped as objects...

        List<ItemChangelog> changelogs = client.getItemChangelog(NAACCR_23, "race1");
        Assert.assertEquals(2, changelogs.size());
        Assert.assertEquals("Modification", changelogs.get(0).getOperation());
        Assert.assertEquals("AllowedCodes", changelogs.get(0).getModifiedAttribute());
        Assert.assertNotNull(changelogs.get(0).getOldValue());
        Assert.assertNotNull(changelogs.get(0).getNewValue());
        Assert.assertEquals("Modification", changelogs.get(1).getOperation());
        Assert.assertEquals("Description", changelogs.get(1).getModifiedAttribute());
        Assert.assertNotNull(changelogs.get(1).getOldValue());
        Assert.assertNotNull(changelogs.get(1).getNewValue());

        // get getting the changelog by item number
        Assert.assertFalse(client.getItemChangelog(NAACCR_23, 160).isEmpty());
    }

    @Test
    public void testGetItemHistory() throws IOException {
        NaaccrApiClient client = NaaccrApiClient.getInstance();

        Map<String, String> map = new LinkedHashMap<>();
        for (ItemHistory history : client.getItemHistory("uihoFacility", NaaccrItemAttribute.ITEM_NAME))
            map.put(history.getNaaccrVersion(), history.getValue());
        Assert.assertEquals("UIHO City", map.get(NAACCR_23));
        Assert.assertEquals("Urban Indian Organization (UIO) Service Area", map.get(NAACCR_24));

        // get getting the history by item number
        Assert.assertFalse(client.getItemHistory(285, NaaccrItemAttribute.ITEM_NAME).isEmpty());
    }
}
