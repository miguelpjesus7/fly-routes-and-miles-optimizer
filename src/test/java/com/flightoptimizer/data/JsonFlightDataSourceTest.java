package com.flightoptimizer.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flightoptimizer.domain.Airport;
import com.flightoptimizer.domain.FlightSegment;
import com.flightoptimizer.domain.Money;
import com.flightoptimizer.domain.SearchQuery;
import com.flightoptimizer.integration.FlightDataMapper;
import com.flightoptimizer.integration.JsonRawSearchDataLoader;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JsonFlightDataSourceTest {

    private final Airport gru = new Airport("GRU", "São Paulo");
    private final Airport rec = new Airport("REC", "Recife");

    private final Money budget = new Money(
        new BigDecimal("2000.00"),
        Currency.getInstance("BRL")
    );

    private final JsonRawSearchDataLoader loader =
        new JsonRawSearchDataLoader(new ObjectMapper());

    private final FlightDataMapper flightDataMapper =
        new FlightDataMapper();

    private final JsonFlightDataSource dataSource =
        new JsonFlightDataSource(
            loader,
            flightDataMapper,
            "sample-search-data.json"
        );

    @Test
    void returnsFlightsFromQueryDepartureDate() {
        SearchQuery query = queryFor(
            LocalDate.of(2026, 9, 10)
        );

        List<FlightSegment> result =
            dataSource.findAllCandidates(query);

        assertEquals(3, result.size());

        assertEquals("LA3000", result.get(0).getFlightNumber());
        assertEquals("LA3001", result.get(1).getFlightNumber());
        assertEquals("LA3002", result.get(2).getFlightNumber());

        assertTrue(
            result.stream().noneMatch(
                segment -> segment.getFlightNumber().equals("LA3003")
            )
        );
    }

    @Test
    void returnsEmptyListWhenNoFlightMatchesQueryDate() {
        SearchQuery query = queryFor(
            LocalDate.of(2026, 9, 12)
        );

        List<FlightSegment> result =
            dataSource.findAllCandidates(query);

        assertEquals(List.of(), result);
    }

    @Test
    void rejectsNullQuery() {
        assertThrows(
            IllegalArgumentException.class,
            () -> dataSource.findAllCandidates(null)
        );
    }

    @Test
    void returnsImmutableCandidates() {
        SearchQuery query = queryFor(
            LocalDate.of(2026, 9, 10)
        );

        List<FlightSegment> result =
            dataSource.findAllCandidates(query);

        assertThrows(
            UnsupportedOperationException.class,
            () -> result.add(exampleFlight())
        );
    }

    @Test
    void rejectsNullLoader() {
        assertThrows(
            IllegalArgumentException.class,
            () -> new JsonFlightDataSource(
                null,
                flightDataMapper,
                "sample-search-data.json"
            )
        );
    }

    @Test
    void rejectsNullFlightDataMapper() {
        assertThrows(
            IllegalArgumentException.class,
            () -> new JsonFlightDataSource(
                loader,
                null,
                "sample-search-data.json"
            )
        );
    }

    @Test
    void rejectsNullResourceName() {
        assertThrows(
            IllegalArgumentException.class,
            () -> new JsonFlightDataSource(
                loader,
                flightDataMapper,
                null
            )
        );
    }

    @Test
    void rejectsBlankResourceName() {
        assertThrows(
            IllegalArgumentException.class,
            () -> new JsonFlightDataSource(
                loader,
                flightDataMapper,
                " "
            )
        );
    }

    private SearchQuery queryFor(LocalDate departureDate) {
        return new SearchQuery(
            gru,
            rec,
            departureDate,
            2,
            budget
        );
    }

    private FlightSegment exampleFlight() {
        return new FlightSegment(
            "AD4000",
            gru,
            rec,
            LocalDateTime.of(2026, 9, 10, 8, 0),
            LocalDateTime.of(2026, 9, 10, 11, 0)
        );
    }
}