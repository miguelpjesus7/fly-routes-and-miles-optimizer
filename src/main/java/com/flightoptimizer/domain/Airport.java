package com.flightoptimizer.domain;

public class Airport{
	private final String iataCode;
	private final String city;

	public Airport(String iataCode, String city){
		this.iataCode = iataCode;
		this.city = city;
	}

	public String getIataCode(){
		return iataCode;
	}

	public String getCity(){
		return city;
	}
}
