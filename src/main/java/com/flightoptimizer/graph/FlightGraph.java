package com.flightoptimizer.graph;

import com.flightoptimizer.domain.Airport;
import com.flightoptimizer.domain.FlightSegment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FlightGraph{
	
	private final Map<Airport, List<FlightSegment>>
			adjacencyList = new HashMap<>();

	public void addFlight(FlightSegment flight){

		adjacencyList
			.computeIfAbsent(
				flight.getOrigin(),
				airport -> new ArrayList<>()
		)
		.add(flight);
	}

	public List<FlightSegment> getFlightsFrom(
			Airport airport	
	) {
		return adjacencyList.getOrDefault(
			airport,
			List.of()
		);
	}
}
