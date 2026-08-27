package com.flightoptimizer.domain;

import java.util.List;

public class AirportGroup {

	private final String code;
	private final String city;
	private final List<Airport> airports;

	public AirportGroup(
			String code,
			String city,
			List<Airport> airports
	){
		this.code = code;
		this.city = city;
		this.airports = List.copyOf(airports);
	}

	public String getCode(){
		return code;
	}

	public String getCity(){
		return city;
	}

	public List<Airport> getAirports(){
		return airports;
	}
}
