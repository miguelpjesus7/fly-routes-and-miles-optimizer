package com.flightoptimizer.domain;

import java.time.LocalDateTime;
import java.util.Objects;

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

	@Override 
	public boolean equals(Object obj){
		if(this == obj){
			return true;
		}

		if(!(obj instanceof FlightSegment other)){
			return false;
		}

		return Objects.equals(flightNumber, other.flightNumber) 
			&& Objects.equals(destination, other.destination)
			&& Objects.equals(origin, other.origin)
			&& Objects.equals(departureTime, other.departureTime)
			&& Objects.equals(arrivalTime, other.arrivalTime);

	}

	@Override 
	public int hashCode(){
		return Objects.hash(
			flightNumber,
			origin,
			destination,
			departureTime,
			arrivalTime
		);
	}
}
