package com.flightoptimizer.integration;

import com.flightoptimizer.domain.CashOffer;
import com.flightoptimizer.domain.Itinerary;
import com.flightoptimizer.domain.FlightSegment;
import com.flightoptimizer.domain.Money;

import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.math.BigDecimal;

public class CashOfferDataMapper {

    private final FlightDataMapper flightDataMapper;

    public CashOfferDataMapper(
        FlightDataMapper flightDataMapper
    ){
        if(flightDataMapper == null){
            throw new IllegalArgumentException();
        }
        this.flightDataMapper = flightDataMapper;
    }

    public CashOffer toDomain(RawCashOfferData rawCashOffer){
        if(rawCashOffer == null){
            throw new IllegalArgumentException();
        }
        if(rawCashOffer.segments() == null){
            throw new IllegalArgumentException();
        }

        List<FlightSegment> segments = new ArrayList<>();

        for(RawFlightData rawSegment : rawCashOffer.segments()){
            segments.add(flightDataMapper.toDomain(rawSegment));
        }

        Currency currency = Currency.getInstance(rawCashOffer.currencyCode());

        return new CashOffer(
            new Itinerary(segments),
            new Money(new BigDecimal(rawCashOffer.fareAmount()), currency),
            new Money(new BigDecimal(rawCashOffer.taxesAmount()), currency)
        );
    }
    
}
