package com.flightoptimizer.integration;

import java.util.List;

public record RawCashOfferData(
    List<RawFlightData> segments,
    String fareAmount,
    String taxesAmount,
    String currencyCode
){   
}
