package com.flightoptimizer.integration;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JsonRawSearchDataLoaderTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final JsonRawSearchDataLoader loader =
        new JsonRawSearchDataLoader(objectMapper);

    @Test
    void loadsValidSearchDataFromClasspath() {
        RawSearchData result =
            loader.loadFromClasspath("sample-search-data.json");

        assertEquals(4, result.flights().size());
        assertEquals(3, result.cashOffers().size());
        assertEquals(1, result.milesOffers().size());

        assertEquals(
            "LA3000",
            result.flights().get(0).flightNumber()
        );

        assertEquals(
            "500.00",
            result.cashOffers().get(0).fareAmount()
        );

        assertEquals(
            "LATAM Pass",
            result.milesOffers().get(0).milesProgram()
        );
    }

    @Test
    void rejectsMissingResource() {
        assertThrows(
            IllegalArgumentException.class,
            () -> loader.loadFromClasspath(
                "missing-search-data.json"
            )
        );
    }

    @Test
    void rejectsNullResourceName() {
        assertThrows(
            IllegalArgumentException.class,
            () -> loader.loadFromClasspath(null)
        );
    }

    @Test
    void rejectsMalformedJson() {
        assertThrows(
            IllegalArgumentException.class,
            () -> loader.loadFromClasspath(
                "invalid-search-data.json"
            )
        );
    }

    @Test
    void rejectsNullObjectMapper() {
        assertThrows(
            IllegalArgumentException.class,
            () -> new JsonRawSearchDataLoader(null)
        );
    }
}