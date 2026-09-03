package com.flightoptimizer.domain;

import java.util.List;

public record Itinerary (
    List<FlightSegment> segments
){
    public Itinerary{
        if(segments == null){
            throw new IllegalArgumentException("Itinerary must be not null");
        }
        if(segments.isEmpty()){
            throw new IllegalArgumentException("Itinerary must be not empty");
        }

        segments = List.copyOf(segments);

        for(int i=0; i< segments.size() - 1; i++){
            FlightSegment actual = segments.get(i);
            FlightSegment next = segments.get(i+1);
            
            if(
                !actual.getDestination().equals(next.getOrigin())
                || next.getDepartureTime().isBefore(actual.getArrivalTime())
            ){
                throw new IllegalArgumentException("");
            }
        }
    }

    public Airport origin(){
        return segments.get(0).getOrigin();
    }

    public Airport destination(){
        return segments.get(segments.size()-1).getDestination();
    }
}