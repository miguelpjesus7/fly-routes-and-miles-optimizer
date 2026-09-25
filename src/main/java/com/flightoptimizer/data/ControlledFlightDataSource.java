package com.flightoptimizer.data;

import com.flightoptimizer.domain.FlightSegment;
import com.flightoptimizer.domain.SearchQuery;

import java.util.ArrayList;
import java.util.List;

public class ControlledFlightDataSource  implements FlightSourceData{

    private final List<FlightSegment> availableFlights;
    
    public ControlledFlightDataSource(
        List<FlightSegment> availableFlights
    ){
        if(availableFlights==null){
            throw new IllegalArgumentException("available flights must not be null");
        }

        this.availableFlights = List.copyOf(availableFlights);
    }

    public List<FlightSegment> findAllCandidates(SearchQuery query){
        if(query == null){
            throw new IllegalArgumentException("query must not be null");
        }

        List<FlightSegment> result = new ArrayList<>();

        for(FlightSegment flight : availableFlights){
            if(flight.getDepartureTime().toLocalDate().equals(query.departureDate())){
                result.add(flight);
            }
        }

        return List.copyOf(result);
    }
}
