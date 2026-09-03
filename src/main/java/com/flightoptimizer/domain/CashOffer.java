package com.flightoptimizer.domain;

public record CashOffer (
    Itinerary itinerary,
    Money fare,
    Money taxes
)implements FlightOffer {
    public CashOffer {
        if(itinerary==null){
            throw new IllegalArgumentException("itinerary must not be null");
        }
        if(fare==null){
            throw new IllegalArgumentException("fare must not be null");
        }
        if(taxes==null){
            throw new IllegalArgumentException("taxes must not be null");
        }
        if(!fare.currency().equals(taxes.currency())){
            throw new IllegalArgumentException("");
        }
    }
}