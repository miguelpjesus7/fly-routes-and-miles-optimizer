package com.flightoptimizer.domain;

import java.time.LocalDateTime;

public class FlightSegment{
	
	private final String flightNumber;

	private final Airport origin;
	private final Airport destination;

	private final LocalDateTime departureTime;
	private final LocalDateTime arrivalTime;

	public FlightSegment(
		String flightNumber,
		Airport origin,
		Airport destination,
		LocalDateTime departureTime,
		LocalDateTime arrivalTime
	){
		this.flightNumber = flightNumber;
		this.origin = origin;
		this.destination = destination;
		this.departureTime = departureTime;
		this.arrivalTime = arrivalTime;
	}

	public String getFlightNumber(){
		return flightNumber;
	}

	public Airport getOrigin(){
		return origin;
	}

	public Airport getDestination(){
		return destination;
	}

	public LocalDateTime getDepartureTime(){
		return departureTime;
	}

	public LocalDateTime getArrivalTime(){
		return arrivalTime;
	}
}
