package com.flightoptimizer;

import com.flightoptimizer.domain.Airport;
import com.flightoptimizer.domain.FlightSegment;
import com.flightoptimizer.graph.FlightGraph;

import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) {

        Airport gru =
                new Airport("GRU", "São Paulo");

        Airport bsb =
                new Airport("BSB", "Brasília");

        Airport cnf =
                new Airport("CNF", "Belo Horizonte");


        FlightSegment flight1 =
                new FlightSegment(
                        "LA3000",
                        gru,
                        bsb,
                        LocalDateTime.of(
                                2026, 9, 10,
                                10, 30
                        ),
                        LocalDateTime.of(
                                2026, 9, 10,
                                12, 15
                        )
                );


        FlightSegment flight2 =
                new FlightSegment(
                        "LA3002",
                        gru,
                        bsb,
                        LocalDateTime.of(
                                2026, 9, 10,
                                13, 30
                        ),
                        LocalDateTime.of(
                                2026, 9, 10,
                                15, 15
                        )
                );


        FlightSegment flight3 =
                new FlightSegment(
                        "AD4100",
                        gru,
                        cnf,
                        LocalDateTime.of(
                                2026, 9, 10,
                                9, 0
                        ),
                        LocalDateTime.of(
                                2026, 9, 10,
                                10, 20
                        )
                );


        FlightGraph graph =
                new FlightGraph();

        graph.addFlight(flight1);
        graph.addFlight(flight2);
        graph.addFlight(flight3);


        for (FlightSegment currentFlight :
                graph.getFlightsFrom(gru)) {

            System.out.println(
                    currentFlight.getFlightNumber()
                    + " / "
                    + currentFlight.getOrigin() 
                    + " -> "
                    + currentFlight.getDestination()
                    + " | "
                    + currentFlight.getDepartureTime()
            );
        }
    }
}
