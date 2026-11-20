package com.flightoptimizer.integration;

import java.math.BigDecimal;
import java.time.format.DateTimeParseException;
import java.time.Instant;
import java.util.Currency;

import com.flightoptimizer.domain.MilesValuation;
import com.flightoptimizer.domain.Money;

public class MilesValuationDataMapper {

    private Instant parseObservedAt(String value){
        if(value == null){
            throw new IllegalArgumentException();
        }

        try{
            return Instant.parse(value);
        }catch(DateTimeParseException exception){
            throw new IllegalArgumentException(
                "observed at must follow ISO-8601 instant format",
                exception
            );
        }
    }

    public MilesValuation toDomain(RawMilesValuationData rawMilesValuationData){
        if(rawMilesValuationData == null){
            throw new IllegalArgumentException();
        }


        return new MilesValuation(
            rawMilesValuationData.program(), 
            new Money(
                new BigDecimal(rawMilesValuationData.pricePerThousandMilesAmount()), 
                Currency.getInstance(rawMilesValuationData.currencyCode())
            ), 
            parseObservedAt(rawMilesValuationData.observedAt())
        );
    }
}
