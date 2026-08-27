package com.flightoptimizer.domain;

public class ConnectionValidator{

	private final long  MCT = 45;

	public boolean canConnect(
		FlightSegment first,
		FlightSegment second
	) {
		return first.getDestination().equals(second.getOrigin())
			&& !second.getDepartureTime()
					 .isBefore(first.getArrivalTime().plusMinutes(MCT));
	}
}
