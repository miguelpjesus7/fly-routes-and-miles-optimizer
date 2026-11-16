package com.flightoptimizer.data;

import java.util.ArrayList;
import java.util.List;

import com.flightoptimizer.domain.SearchQuery;
import com.flightoptimizer.domain.FlightSegment;
import com.flightoptimizer.integration.FlightDataMapper;
import com.flightoptimizer.integration.JsonRawSearchDataLoader;
import com.flightoptimizer.integration.RawFlightData;
import com.flightoptimizer.integration.RawSearchData;

public class JsonFlightDataSource implements FlightSourceData{

    private final JsonRawSearchDataLoader loader;
    private final FlightDataMapper flightDataMapper;
    private final String resourceName;

    public JsonFlightDataSource(
        JsonRawSearchDataLoader loader,
        FlightDataMapper flightDataMapper,
        String resourceName
    ){
        if(loader == null){
            throw new IllegalArgumentException();
        }
        if(flightDataMapper == null){
            throw new IllegalArgumentException();
        }
        if(resourceName == null || resourceName.isBlank()){
            throw new IllegalArgumentException();
        }

        this.loader = loader;
        this.flightDataMapper = flightDataMapper;
        this.resourceName = resourceName;
    }

    @Override 
    public List<FlightSegment> findAllCandidates(SearchQuery query){
        if(query == null){
            throw new IllegalArgumentException();
        }

        RawSearchData searchData = loader.loadFromClasspath(resourceName);
        List<FlightSegment> candidateFlights = new ArrayList<>();

        for (RawFlightData rawFlight : searchData.flights()) {
            FlightSegment segment = flightDataMapper.toDomain(rawFlight);

            if (segment.getDepartureTime()
                .toLocalDate()
                .equals(query.departureDate())) {
                candidateFlights.add(segment);
            }
        }

        return List.copyOf(candidateFlights);
    }
}
