# NAACCR API Client

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=imsweb_naaccr-api-client&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=imsweb_naaccr-api-client)
[![integration](https://github.com/imsweb/naaccr-api-client/workflows/integration/badge.svg)](https://github.com/imsweb/naaccr-api-client/actions)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.imsweb/naaccr-api-client/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.imsweb/naaccr-api-client)

This library allows to call the NAACCR API using Java DTOs.

By default, it uses version 1.0 of the public API (https://apps.naaccr.org/data-dictionary/api/).

## Download

The library is available on [Maven Central](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.imsweb%22%20AND%20a%3A%22naaccr-api-client%22).

To include it to your Maven or Gradle project, use the group ID `com.imsweb` and the artifact ID `naaccr-api-client`.

You can check out the [release page](https://github.com/imsweb/naaccr-api-client/releases) for a list of the releases and their changes.

## Usage

Create a client (using the NaaccrApiClient::getInstance method) and call the few methods that correspond to the API end points.

```java
NaaccrApiClient client = NaaccrApiClient.getInstance();
NaaccrDataItem item = client.getDataItem(NAACCR_LATEST, "primarySite");
System.out.println(item.getItemName());
```

For a comprehensive list of available methods, see the javadocs of the 
[NaaccrApiClient](https://github.com/imsweb/naaccr-api-client/blob/main/src/main/java/com/imsweb/naaccr/api/client/NaaccrApiClient.java) class.

## About this library

This library was developed through the [SEER](http://seer.cancer.gov/) program.

The Surveillance, Epidemiology and End Results program is a premier source for cancer statistics in the United States.
The SEER program collects information on incidence, prevalence and survival from specific geographic areas representing
a large portion of the US population and reports on all these data plus cancer mortality data for the entire country.