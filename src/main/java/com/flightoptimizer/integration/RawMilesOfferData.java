package com.flightoptimizer.integration;

import java.util.List;

public record RawMilesOfferData(

    List<RawFlightData> segments,
    String milesAmount,
    String milesProgram,
    String taxesAmount,
    String currencyCode
) 
{
    
}
