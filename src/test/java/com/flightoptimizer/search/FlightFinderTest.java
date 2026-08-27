package com.flightoptimizer.search;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.flightoptimizer.domain.Airport;
import com.flightoptimizer.domain.ConnectionValidator;
import com.flightoptimizer.domain.FlightSegment;
import com.flightoptimizer.graph.FlightGraph;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class FlightFinderTest {

    private final Airport gru = new Airport("GRU", "São Paulo");
    private final Airport cnf = new Airport("CNF", "Belo Horizonte");
    private final Airport bsb = new Airport("BSB", "Brasília");

    @Test
    void findsDirectRoute() {
        FlightSegment directFlight = flight("LA3000", gru, bsb, 10, 30, 12, 15);
        FlightGraph graph = graphWith(directFlight);

        Optional<List<FlightSegment>> route = finder(graph).findRoute(gru, bsb, 1);

        assertEquals(Optional.of(List.of(directFlight)), route);
    }

    @Test
    void findsRouteWithOneValidConnection() {
        FlightSegment firstFlight = flight("AD4100", gru, cnf, 9, 0, 10, 20);
        FlightSegment secondFlight = flight("AD4101", cnf, bsb, 11, 30, 12, 50);
        FlightGraph graph = graphWith(firstFlight, secondFlight);

        Optional<List<FlightSegment>> route = finder(graph).findRoute(gru, bsb, 2);

        assertEquals(Optional.of(List.of(firstFlight, secondFlight)), route);
    }

    @Test
    void rejectsConnectionThatDoesNotMeetMinimumConnectionTime() {
        FlightSegment firstFlight = flight("AD4100", gru, cnf, 9, 0, 10, 20);
        FlightSegment tooEarlyFlight = flight("AD4101", cnf, bsb, 10, 50, 12, 10);
        FlightGraph graph = graphWith(firstFlight, tooEarlyFlight);

        Optional<List<FlightSegment>> route = finder(graph).findRoute(gru, bsb, 2);

        assertTrue(route.isEmpty());
    }

    @Test
    void respectsMaximumSegments() {
        FlightSegment firstFlight = flight("AD4100", gru, cnf, 9, 0, 10, 20);
        FlightSegment secondFlight = flight("AD4101", cnf, bsb, 11, 30, 12, 50);
        FlightGraph graph = graphWith(firstFlight, secondFlight);

        Optional<List<FlightSegment>> route = finder(graph).findRoute(gru, bsb, 1);

        assertTrue(route.isEmpty());
    }

    private FlightFinder finder(FlightGraph graph) {
        return new FlightFinder(graph, new ConnectionValidator());
    }

    private FlightGraph graphWith(FlightSegment... flights) {
        FlightGraph graph = new FlightGraph();
        for (FlightSegment flight : flights) {
            graph.addFlight(flight);
        }
        return graph;
    }

    private FlightSegment flight(String number, Airport origin, Airport destination,
                                 int departureHour, int departureMinute,
                                 int arrivalHour, int arrivalMinute) {
        return new FlightSegment(
                number,
                origin,
                destination,
                LocalDateTime.of(2026, 9, 10, departureHour, departureMinute),
                LocalDateTime.of(2026, 9, 10, arrivalHour, arrivalMinute));
    }
}
