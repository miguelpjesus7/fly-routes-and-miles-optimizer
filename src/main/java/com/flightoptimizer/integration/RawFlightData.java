package com.flightoptimizer.integration;

public record RawFlightData(
    String flightNumber,
    String originIataCode,
    String originCity,
    String destinationIataCode,
    String destinationCity,
    String departureDateTime,
    String arrivalDateTime

) {

}
