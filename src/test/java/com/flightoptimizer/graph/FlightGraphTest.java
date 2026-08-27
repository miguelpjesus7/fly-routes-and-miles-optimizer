package com.flightoptimizer.graph;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.flightoptimizer.domain.Airport;
import com.flightoptimizer.domain.FlightSegment;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;

class FlightGraphTest {

    @Test
    void preservesMultipleFlightsLeavingFromTheSameAirport() {
        Airport gru = new Airport("GRU", "São Paulo");
        Airport bsb = new Airport("BSB", "Brasília");
        FlightSegment morningFlight = flight("LA3000", gru, bsb, 10, 30);
        FlightSegment afternoonFlight = flight("LA3002", gru, bsb, 13, 30);
        FlightGraph graph = new FlightGraph();

        graph.addFlight(morningFlight);
        graph.addFlight(afternoonFlight);

        assertEquals(List.of(morningFlight, afternoonFlight), graph.getFlightsFrom(gru));
    }

    @Test
    void returnsEmptyListWhenAirportHasNoDepartures() {
        FlightGraph graph = new FlightGraph();

        assertTrue(graph.getFlightsFrom(new Airport("BSB", "Brasília")).isEmpty());
    }

    private FlightSegment flight(String number, Airport origin, Airport destination,
                                 int departureHour, int departureMinute) {
        LocalDateTime departure = LocalDateTime.of(2026, 9, 10, departureHour, departureMinute);
        return new FlightSegment(number, origin, destination, departure, departure.plusHours(1));
    }
}
