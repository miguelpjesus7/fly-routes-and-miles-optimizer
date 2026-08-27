package com.flightoptimizer.search;

import com.flightoptimizer.domain.Airport;
import com.flightoptimizer.domain.ConnectionValidator;
import com.flightoptimizer.domain.FlightSegment;
import com.flightoptimizer.graph.FlightGraph;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FlightFinderViabilityTest{

	private final Airport gru = new Airport("GRU", "São Paulo");
	private final Airport cnf = new Airport("CNF", "Belo Horizonte");
	private final Airport bsb = new Airport("BSB", "Brasília");
	private final Airport rec = new Airport("REC", "Recife");
	private final Airport mcz = new Airport("MCZ", "Maceió");

	@Test
	void returnAllRoutes(){
		FlightSegment firstFlight = flight("LA3000", gru, bsb, 10, 00, 11, 30);
		FlightSegment secondFlight = flight("LA3003", gru, rec, 15, 00, 18, 00);
		FlightSegment thirdFlight = flight("LA3004", rec, bsb, 19, 00, 21, 00);

		FlightGraph graph = graphWith(firstFlight, secondFlight, thirdFlight);

		List<List<FlightSegment>> allRoutes = finder(graph).findAllRoutes(gru, bsb, 2);

		assertEquals(List.of(
					List.of(firstFlight), 
					List.of(secondFlight, thirdFlight)
				), 
				allRoutes
			);
	}

	@Test
	void notReturnCycle(){
		FlightSegment firstFlight = flight("LA3001", gru, cnf, 10, 00, 11, 30);
		FlightSegment secondFlight = flight("LA3002", cnf, gru, 15, 00, 17, 30);
		FlightSegment thirdFlight = flight("LA3003", gru, bsb, 19, 00, 21, 30);
		FlightSegment fourthFlight = flight("LA3004", bsb, mcz, 22, 20, 23, 40);

		FlightGraph graph = graphWith(firstFlight, secondFlight, thirdFlight, fourthFlight);

		List<List<FlightSegment>> allRoutes = finder(graph).findAllRoutes(gru, mcz, 4);

		assertEquals(
			List.of(List.of(thirdFlight, fourthFlight)),
			allRoutes
		);
	}	

	@Test
	void returnRoutesRespectedmaximumSegments(){
		FlightSegment firstFlight = flight("LA3000", gru, cnf, 10, 00, 11, 00);
		FlightSegment secondFlight = flight("LA3001", cnf, rec, 12, 00, 13, 00);
		FlightSegment thirdFlight = flight("LA3002", rec, bsb, 14, 00, 15, 00);
		FlightSegment fourthFlight = flight("LA3003", bsb, mcz, 16, 00, 17, 00);
		FlightSegment fifthFlight = flight("LA3004", rec, mcz, 14, 00, 15, 30);

		FlightGraph graph = graphWith(firstFlight, secondFlight, thirdFlight, fourthFlight, fifthFlight);

		List<List<FlightSegment>> allRoutes = finder(graph).findAllRoutes(gru, mcz, 2);

		assertTrue(allRoutes.isEmpty());
	}

	@Test
	void resultIsException(){
		FlightSegment firstFlight = flight("LA3000", gru, cnf, 10, 00, 11, 00);

		FlightGraph graph = graphWith(firstFlight);


		assertThrows(IllegalArgumentException.class, () -> finder(graph).findAllRoutes(gru, cnf, 0));
	} 

	@Test
	void dontReturnRouteWhenWasMCTViolation() {
		FlightSegment firstFlight = flight("LA3000", gru, cnf, 10, 00, 11, 00);
		FlightSegment secondFlight = flight("LA3001", cnf, rec, 11, 30, 13, 00);

		FlightGraph graph = graphWith(firstFlight, secondFlight);

		List<List<FlightSegment>> allRoutes = finder(graph).findAllRoutes(gru, rec, 2);
		
		assertTrue(allRoutes.isEmpty());
	}

    private FlightFinderViability finder(FlightGraph graph) {
        return new FlightFinderViability(graph, new ConnectionValidator());
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
