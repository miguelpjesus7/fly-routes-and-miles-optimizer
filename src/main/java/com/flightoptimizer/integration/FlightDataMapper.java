package com.flightoptimizer.integration;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.LocalDateTime;

import com.flightoptimizer.domain.FlightSegment;
import com.flightoptimizer.domain.Airport;

public class FlightDataMapper{

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    private LocalDateTime parseDateTime(String value, String fieldName){
        try{
            return LocalDateTime.parse(value, DATE_TIME_FORMATTER);
        }catch (DateTimeParseException exception){
            throw new IllegalArgumentException(
                fieldName + "must follow expected date-time format",  
                exception
            );
        }
    }

    public FlightSegment toDomain(RawFlightData rawFlightData){
        if(rawFlightData == null){
            throw new IllegalArgumentException();
        }

        LocalDateTime departureDateTime = parseDateTime(rawFlightData.departureDateTime(), "departure date-time");
        LocalDateTime arrivalDateTime = parseDateTime(rawFlightData.arrivalDateTime(), "arrival date-time"); 

        return new FlightSegment(
            rawFlightData.flightNumber(), 
            new Airport(rawFlightData.originIataCode(), rawFlightData.originCity()), 
            new Airport(rawFlightData.destinationIataCode(), rawFlightData.destinationCity()), 
            departureDateTime, 
            arrivalDateTime
        );
    }

}