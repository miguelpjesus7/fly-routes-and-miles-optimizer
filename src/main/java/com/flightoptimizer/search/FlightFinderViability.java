package com.flightoptimizer.search;

import com.flightoptimizer.domain.FlightSegment;
import com.flightoptimizer.domain.Itinerary;
import com.flightoptimizer.domain.ConnectionValidator;
import com.flightoptimizer.domain.Airport;
import com.flightoptimizer.graph.FlightGraph;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;


public class FlightFinderViability{

	private final FlightGraph graph;
	private final ConnectionValidator validator;

	public FlightFinderViability(
		FlightGraph graph,
		ConnectionValidator validator
		
	){
		this.graph = graph;
		this.validator = validator;
		
	}	

	public List<Itinerary> findAllRoutes (Airport origin, Airport destination, int maximumSegments) {
		if (origin == null || destination == null) {
			throw new IllegalArgumentException("Origin and destination airports must not be null");
		}
		if (maximumSegments <= 0) {
			throw new IllegalArgumentException("Maximum segments must be positive");
		}

		List<Itinerary> allRoutes = new ArrayList<>();
		Queue<List<FlightSegment>> frontier = new ArrayDeque<>();
		
		for(FlightSegment firstFlight : graph.getFlightsFrom(origin)){
			frontier.add(List.of(firstFlight));
		}
		
		while(!frontier.isEmpty()){
			List<FlightSegment> currentRoute = frontier.poll();

			FlightSegment lastFlight = currentRoute.get(currentRoute.size() - 1);

			if (lastFlight.getDestination().equals(destination)){
				allRoutes.add(new Itinerary(currentRoute));
				continue;
			}
			
			if(currentRoute.size() == maximumSegments){
				continue;
			}

			for(FlightSegment nextFlight : graph.getFlightsFrom(lastFlight.getDestination()) ){
				if(
					validator.canConnect(lastFlight, nextFlight)
					&& !visitsAirport(currentRoute, nextFlight.getDestination())
				){
					List<FlightSegment> extendedRoute = new ArrayList<>(currentRoute);

					extendedRoute.add(nextFlight);
					frontier.add(extendedRoute);
				}
			}
		}

		return allRoutes;
	
	}

	private boolean visitsAirport(List<FlightSegment> route, Airport airport){
		
		if(route.get(0).getOrigin().equals(airport)){
			return true;
		}
		
		for(FlightSegment segment : route){
			if(segment.getDestination().equals(airport)){
				return true;
			}
		}
		return false;
	}

	
}