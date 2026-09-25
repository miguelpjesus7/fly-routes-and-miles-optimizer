package com.flightoptimizer.data;

import com.flightoptimizer.domain.Airport;
import com.flightoptimizer.domain.FlightSegment;
import com.flightoptimizer.domain.Money;
import com.flightoptimizer.domain.SearchQuery;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ControlledFlightDataSourceTest {

    private final Airport gru = new Airport("GRU", "São Paulo");
    private final Airport bsb = new Airport("BSB", "Brasília");
    private final Airport rec = new Airport("REC", "Recife");

    private final LocalDate searchDate = LocalDate.of(2026, 9, 10);
    private final LocalDate anotherDate = LocalDate.of(2026, 9, 11);

    private final Money budget = new Money( new BigDecimal("2000.00"), Currency.getInstance("BRL"));

    private final SearchQuery query = new SearchQuery(gru, rec, searchDate, 2, budget);

    private final FlightSegment firstFlight = flight("LA3000", gru, bsb, searchDate, 10, 0, 11, 30);
    private final FlightSegment connectionFlight = flight( "LA3001", bsb, rec, searchDate, 13, 0, 15, 0);
    private final FlightSegment flightFromAnotherDate = flight("LA3002", gru, rec, anotherDate, 10, 0, 13, 0);

    @Test
    void returnsAllFlightsFromRequestedDate() {
        ControlledFlightDataSource dataSource = new ControlledFlightDataSource(List.of(firstFlight, connectionFlight, flightFromAnotherDate));
        assertEquals(
            List.of(firstFlight, connectionFlight),
            dataSource.findAllCandidates(query)
        );
    }

    @Test
    void includesConnectionFlightsWithDifferentOrigin() {
        ControlledFlightDataSource dataSource = new ControlledFlightDataSource(List.of(firstFlight, connectionFlight));
        assertEquals(
            List.of(firstFlight, connectionFlight),
            dataSource.findAllCandidates(query)
        );
    }

    @Test
    void excludesFlightsFromDifferentDate() {
        ControlledFlightDataSource dataSource = new ControlledFlightDataSource(List.of(firstFlight, flightFromAnotherDate));
        assertEquals(
            List.of(firstFlight),
            dataSource.findAllCandidates(query)
        );
    }

    @Test
    void returnsEmptyListWhenThereAreNoFlightsOnRequestedDate() {
        ControlledFlightDataSource dataSource = new ControlledFlightDataSource(List.of(flightFromAnotherDate));
        assertEquals(
            List.of(),
            dataSource.findAllCandidates(query)
        );
    }

    @Test
    void rejectsNullAvailableFlights() {
        assertThrows(
            IllegalArgumentException.class,
            () -> new ControlledFlightDataSource(null)
        );
    }

    @Test
    void rejectsNullQuery() {
        ControlledFlightDataSource dataSource =
            new ControlledFlightDataSource(List.of(firstFlight));

        assertThrows(
            IllegalArgumentException.class,
            () -> dataSource.findAllCandidates(null)
        );
    }

    @Test
    void returnsImmutableResult() {
        ControlledFlightDataSource dataSource = new ControlledFlightDataSource(List.of(firstFlight));
        List<FlightSegment> result = dataSource.findAllCandidates(query);

        assertThrows(
            UnsupportedOperationException.class,
            () -> result.add(connectionFlight)
        );
    }

    @Test
    void protectsInternalStateFromChangesInOriginalList() {
        List<FlightSegment> mutableFlights = new ArrayList<>();
        mutableFlights.add(firstFlight);

        ControlledFlightDataSource dataSource = new ControlledFlightDataSource(mutableFlights);

        mutableFlights.add(connectionFlight);

        assertEquals(
            List.of(firstFlight),
            dataSource.findAllCandidates(query)
        );
    }

    private FlightSegment flight(
        String flightNumber,
        Airport origin,
        Airport destination,
        LocalDate date,
        int departureHour,
        int departureMinute,
        int arrivalHour,
        int arrivalMinute
    ) {
        return new FlightSegment(
            flightNumber,
            origin,
            destination,
            LocalDateTime.of(
                date,
                java.time.LocalTime.of(
                    departureHour,
                    departureMinute
                )
            ),
            LocalDateTime.of(
                date,
                java.time.LocalTime.of(
                    arrivalHour,
                    arrivalMinute
                )
            )
        );
    }
}