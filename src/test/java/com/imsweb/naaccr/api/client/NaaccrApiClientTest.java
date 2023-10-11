/*
 * Copyright (C) 2023 Information Management Services, Inc.
 */
package com.imsweb.naaccr.api.client;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;

import com.imsweb.naaccr.api.client.NaaccrApiClient.NaaccrItemAttribute;
import com.imsweb.naaccr.api.client.entity.ItemHistory;
import com.imsweb.naaccr.api.client.entity.NaaccrAllowedCode;
import com.imsweb.naaccr.api.client.entity.NaaccrDataItem;
import com.imsweb.naaccr.api.client.entity.NaaccrVersion;

import static com.imsweb.naaccr.api.client.NaaccrApiClient.NAACCR_23;
import static com.imsweb.naaccr.api.client.NaaccrApiClient.NAACCR_24;

public class NaaccrApiClientTest {

    // https://apps.naaccr.org/data-dictionary/api/1.0/documentation/

    // TODO FD implement last history call

    @Test
    public void testGetNaaccrVersions() throws IOException {
        NaaccrApiClient client = NaaccrApiClient.getInstance();
        Set<String> versions = client.getNaaccrVersions().stream().map(NaaccrVersion::getVersion).collect(Collectors.toSet());
        Assert.assertTrue(versions.toString(), versions.contains("21"));
        for (String v : versions)
            Assert.assertTrue(v, v.matches("\\d{2}"));

        for (NaaccrVersion v : client.getNaaccrVersions())
            System.out.println(v.getVersion() + ": " + v.getYearImplemented() + " - " + v.getDateOfPublication());
    }

    @Test
    public void testGetDataItem() throws IOException {
        NaaccrApiClient client = NaaccrApiClient.getInstance();

        System.out.println(client.getDataItem("23", "1030").getItemName());
        System.out.println(client.getDataItem("23", "1030").getDescription());
        System.out.println(client.getDataItem("23", "1030").getYearImplemented());
        System.out.println(client.getDataItem("23", "1030").getVersionImplemented());
        System.out.println(client.getDataItem("23", "1030").getYearRetired());
        System.out.println(client.getDataItem("23", "1030").getVersionRetired());

        System.out.println("*****");
        System.out.println(client.getDataItem(NAACCR_23, "ageAtDiagnosis").getItemName());
        Assert.assertEquals("Age at Diagnosis", client.getDataItem(NAACCR_23, "ageAtDiagnosis").getItemName());
        System.out.println(client.getDataItem("23", "ageAtDiagnosis").getDateCreated());
        System.out.println(client.getDataItem("23", "ageAtDiagnosis").getDateModified());
        for (NaaccrAllowedCode code : client.getDataItem("23", "ageAtDiagnosis").getAllowedCodes())
            System.out.println(code.getCode() + ": " + code.getDescription());

        //System.out.println(client.getDataItem("23", "reportingFacility").getAlternateNames());
    }

    // commented out because it's too slow
    @Test
    public void testGetDataItems() throws IOException {
        NaaccrApiClient client = NaaccrApiClient.getInstance();

        for (NaaccrDataItem item : client.getDataItems(NAACCR_23))
            System.out.println(item.getItemName());
    }

    @Test
    public void testGetItemHistory() throws IOException {
        NaaccrApiClient client = NaaccrApiClient.getInstance();

        Map<String, String> map = new LinkedHashMap<>();
        for (ItemHistory history : client.getItemHistory("uihoFacility", NaaccrItemAttribute.ITEM_NAME))
            map.put(history.getNaaccrVersion(), history.getValue());

        Assert.assertEquals("UIHO City", map.get(NAACCR_23));
        Assert.assertEquals("Urban Indian Organization (UIO) Service Area", map.get(NAACCR_24));
    }

}
