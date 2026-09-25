package com.flightoptimizer.data;

import com.flightoptimizer.domain.FlightOffer;
import com.flightoptimizer.domain.Itinerary;

import java.util.List;

public interface OfferDataSource {
    List<FlightOffer> findOffers(List<Itinerary> itineraries);
}
