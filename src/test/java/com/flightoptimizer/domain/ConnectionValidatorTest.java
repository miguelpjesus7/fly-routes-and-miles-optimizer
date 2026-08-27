package com.flightoptimizer.domain;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class ConnectionValidatorTest {

    private final Airport gru = new Airport("GRU", "São Paulo");
    private final Airport cnf = new Airport("CNF", "Belo Horizonte");
    private final Airport bsb = new Airport("BSB", "Brasília");
    private final ConnectionValidator validator = new ConnectionValidator();

    @Test
    void acceptsConnectionWhenItHasMoreThanMinimumConnectionTime() {
        FlightSegment arrivingFlight = flight("AD4100", gru, cnf, 9, 0, 10, 20);
        FlightSegment departingFlight = flight("AD4101", cnf, bsb, 11, 30, 12, 50);

        assertTrue(validator.canConnect(arrivingFlight, departingFlight));
    }

    @Test
    void acceptsConnectionWhenItDepartsExactlyAtMinimumConnectionTime() {
        FlightSegment arrivingFlight = flight("AD4100", gru, cnf, 9, 0, 10, 20);
        FlightSegment departingFlight = flight("AD4101", cnf, bsb, 11, 5, 12, 25);

        assertTrue(validator.canConnect(arrivingFlight, departingFlight));
    }

    @Test
    void rejectsConnectionWithInsufficientConnectionTime() {
        FlightSegment arrivingFlight = flight("AD4100", gru, cnf, 9, 0, 10, 20);
        FlightSegment departingFlight = flight("AD4101", cnf, bsb, 11, 4, 12, 24);

        assertFalse(validator.canConnect(arrivingFlight, departingFlight));
    }

    @Test
    void rejectsConnectionWhenAirportsDoNotMatch() {
        FlightSegment arrivingFlight = flight("AD4100", gru, cnf, 9, 0, 10, 20);
        FlightSegment departingFlight = flight("AD4101", gru, bsb, 11, 30, 12, 50);

        assertFalse(validator.canConnect(arrivingFlight, departingFlight));
    }

    private FlightSegment flight(String flightNumber, Airport origin, Airport destination,
                                 int departureHour, int departureMinute,
                                 int arrivalHour, int arrivalMinute) {
        return new FlightSegment(
                flightNumber,
                origin,
                destination,
                LocalDateTime.of(2026, 9, 10, departureHour, departureMinute),
                LocalDateTime.of(2026, 9, 10, arrivalHour, arrivalMinute));
    }
}
