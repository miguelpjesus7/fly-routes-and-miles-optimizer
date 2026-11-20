package com.flightoptimizer.integration;

public record RawMilesValuationData(
    String program,
    String pricePerThousandMilesAmount,
    String currencyCode, 
    String observedAt
) {
    
}
