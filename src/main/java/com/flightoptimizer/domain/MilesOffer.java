package com.flightoptimizer.domain;

public record MilesOffer(
    Itinerary itinerary,
    MilesAmount miles,
    Money taxes
)implements FlightOffer{
    public MilesOffer{
        if(itinerary==null){
            throw new IllegalArgumentException("itinerary must not be null");
        }
        if(miles == null){
            throw new IllegalArgumentException("miles must not be null");
        }
        if(taxes==null){
            throw new IllegalArgumentException("taxes must not be null");
        }
    }
}
