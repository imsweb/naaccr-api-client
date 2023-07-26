/*
 * Copyright (C) 2023 Information Management Services, Inc.
 */
package com.imsweb.naaccr.api.client;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import com.imsweb.naaccr.api.client.entity.NaaccrAllowedCode;
import com.imsweb.naaccr.api.client.entity.NaaccrDataItem;

import static com.imsweb.naaccr.api.client.NaaccrApiClient.NAACCR_23;

public class NaaccrApiClientTest {

    @Test
    public void testGetDataItem() throws IOException {
        NaaccrApiClient client = NaaccrApiClient.getInstance();

        Assert.assertEquals("Age at Diagnosis", client.getDataItem(NAACCR_23, "ageAtDiagnosis").getItemName());
        for (NaaccrAllowedCode code : client.getDataItem("23", "ageAtDiagnosis").getAllowedCodes())
            System.out.println(code.getCode() + ": " + code.getDescription());

        //System.out.println(client.getDataItem("23", "reportingFacility").getAlternateNames());
    }

//    @Test
//    public void testGetDataItems() throws IOException {
//        NaaccrApiClient client = NaaccrApiClient.getInstance();
//
//        for (NaaccrDataItem item : client.getDataItems(NAACCR_23)) {
//            System.out.println(item.getItemName());
//        }
//
//    }
    
}
