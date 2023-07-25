/*
 * Copyright (C) 2023 Information Management Services, Inc.
 */
package com.imsweb.naaccr.api.client;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

public class NaaccrApiClientTest {

    @Test
    public void testGetDataItem() throws IOException {
        NaaccrApiClient client = new NaaccrApiClient.Builder().connect();

        Assert.assertEquals("Age at Diagnosis", client.getService().getDataItem("23", "ageAtDiagnosis").execute().body().getItemName());
    }
    
}
