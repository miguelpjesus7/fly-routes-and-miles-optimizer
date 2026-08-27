package com.flightoptimizer.search;

import com.flightoptimizer.domain.Airport;
import com.flightoptimizer.domain.ConnectionValidator;
import com.flightoptimizer.domain.FlightSegment;
import com.flightoptimizer.graph.FlightGraph;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;

public class FlightFinder {

    private final FlightGraph graph;
    private final ConnectionValidator connectionValidator;

    public FlightFinder(
    		FlightGraph graph, 
    		ConnectionValidator connectionValidator) {
        this.graph = Objects.requireNonNull(graph);
        this.connectionValidator = Objects.requireNonNull(connectionValidator);
    }

    public Optional<List<FlightSegment>> findRoute(
            Airport origin,
            Airport destination,
            int maximumSegments) {
        if (maximumSegments < 1) {
            throw new IllegalArgumentException("maximumSegments must be at least 1");
        }

        Queue<RouteState> frontier = new ArrayDeque<>();

        for (FlightSegment firstFlight : graph.getFlightsFrom(origin)) {
            frontier.offer(new RouteState(List.of(firstFlight)));
        }

        while (!frontier.isEmpty()) {
            RouteState currentRoute = frontier.poll();
            FlightSegment lastFlight = currentRoute.lastFlight();

            if (lastFlight.getDestination().equals(destination)) {
                return Optional.of(currentRoute.path());
            }

            if (currentRoute.path().size() == maximumSegments) {
                continue;
            }

            for (FlightSegment nextFlight : graph.getFlightsFrom(lastFlight.getDestination())) {
                if (connectionValidator.canConnect(lastFlight, nextFlight)) {
                    frontier.offer(currentRoute.append(nextFlight));
                }
            }
        }

        return Optional.empty();
    }

    private record RouteState(List<FlightSegment> path) {

        private RouteState {
            path = List.copyOf(path);
        }

        private FlightSegment lastFlight() {
            return path.getLast();
        }

        private RouteState append(FlightSegment nextFlight) {
            List<FlightSegment> extendedPath = new ArrayList<>(path);
            extendedPath.add(nextFlight);
            return new RouteState(extendedPath);
        }
    }
}
