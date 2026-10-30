package com.flightoptimizer.integration;

import com.flightoptimizer.domain.FlightSegment;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

class FlightDataMapperTest {

    private final FlightDataMapper mapper = new FlightDataMapper();

    @Test
    void mapsValidRawFlightDataToFlightSegment() {
        RawFlightData rawFlight = new RawFlightData(
            "LA3000",
            "GRU",
            "São Paulo",
            "REC",
            "Recife",
            "2026-09-10T08:00:00",
            "2026-09-10T11:00:00"
        );

        FlightSegment result = mapper.toDomain(rawFlight);

        assertEquals("LA3000", result.getFlightNumber());

        assertEquals("GRU", result.getOrigin().getIataCode());
        assertEquals("São Paulo", result.getOrigin().getCity());

        assertEquals("REC", result.getDestination().getIataCode());
        assertEquals("Recife", result.getDestination().getCity());

        assertEquals(
            LocalDateTime.of(2026, 9, 10, 8, 0),
            result.getDepartureTime()
        );

        assertEquals(
            LocalDateTime.of(2026, 9, 10, 11, 0),
            result.getArrivalTime()
        );
    }

    @Test
    void rejectsNullRawFlightData() {
        assertThrows(
            IllegalArgumentException.class,
            () -> mapper.toDomain(null)
        );
    }

    @Test
    void rejectsInvalidDepartureDateTime() {
        RawFlightData rawFlight = new RawFlightData(
            "LA3000",
            "GRU",
            "São Paulo",
            "REC",
            "Recife",
            "invalid-departure-date",
            "2026-09-10T11:00:00"
        );

        assertThrows(
            IllegalArgumentException.class,
            () -> mapper.toDomain(rawFlight)
        );
    }

    @Test
    void rejectsInvalidArrivalDateTime() {
        RawFlightData rawFlight = new RawFlightData(
            "LA3000",
            "GRU",
            "São Paulo",
            "REC",
            "Recife",
            "2026-09-10T08:00:00",
            "invalid-arrival-date"
        );

        assertThrows(
            IllegalArgumentException.class,
            () -> mapper.toDomain(rawFlight)
        );
    }
}