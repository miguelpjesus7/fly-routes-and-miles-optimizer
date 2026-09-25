package com.flightoptimizer.data;

import com.flightoptimizer.domain.FlightOffer;
import com.flightoptimizer.domain.Itinerary;

import java.util.ArrayList;
import java.util.List;

public final class ControlledOfferDataSource implements OfferDataSource {

    private final List<FlightOffer> availableOffers;

    public ControlledOfferDataSource(
        List<FlightOffer> availableOffers
    ){
        if(availableOffers == null){
            throw new IllegalArgumentException("available offers must not be null");
        }
        
        this.availableOffers = List.copyOf(availableOffers);
    }

    @Override 
    public List<FlightOffer> findOffers(List<Itinerary> itineraries){
        if(itineraries == null){
            throw new IllegalArgumentException("itineraries must not be null");
        }

        List<FlightOffer> result = new ArrayList<>();

        for(FlightOffer offer : availableOffers){
            if(itineraries.contains(offer.itinerary())){
                result.add(offer);
            }
        }

        return List.copyOf(result);
    }
}
