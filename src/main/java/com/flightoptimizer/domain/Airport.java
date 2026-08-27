package com.flightoptimizer.domain;

import java.util.Objects;

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

	@Override 
	public String toString(){
		return iataCode + " - " + city;
	}

	public boolean equals(Object obj){
		if(this == obj) return true;

		if(!(obj instanceof Airport airport)){
			return false;
		}

		return Objects.equals(iataCode, airport.iataCode);
	}

	@Override 
	public int hashCode(){
		return Objects.hash(iataCode);
	}
}
