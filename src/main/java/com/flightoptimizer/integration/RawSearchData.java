package com.flightoptimizer.integration;

import java.util.List;

public record RawSearchData(
    List<RawFlightData> flights,
    List<RawCashOfferData> cashOffers,
    List<RawMilesOfferData> milesOffers
) {
    
}
