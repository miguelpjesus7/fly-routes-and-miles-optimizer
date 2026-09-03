package com.flightoptimizer.domain;

import java.time.Instant;

public record MilesValuation(
    String program,
    Money pricePerThousandMiles,
    Instant observedAt
) {
    public MilesValuation{
        if(program == null
            || program.isBlank()
        ){
            throw new IllegalArgumentException();
        }
        if(pricePerThousandMiles == null
            || pricePerThousandMiles.amount().signum() <= 0
        ){
            throw new IllegalArgumentException();   
        }
        if(observedAt == null){
            throw new IllegalArgumentException();
        }
    }
}
