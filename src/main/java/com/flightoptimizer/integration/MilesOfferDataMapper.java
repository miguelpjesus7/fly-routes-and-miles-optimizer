package com.flightoptimizer.integration;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

import com.flightoptimizer.domain.MilesOffer;
import com.flightoptimizer.domain.FlightSegment;
import com.flightoptimizer.domain.Itinerary;
import com.flightoptimizer.domain.MilesAmount;
import com.flightoptimizer.domain.Money;

public class MilesOfferDataMapper {

    private final FlightDataMapper flightDataMapper;

    public MilesOfferDataMapper(
        FlightDataMapper flightDataMapper
    ){
        if(flightDataMapper == null){
            throw new IllegalArgumentException();
        }

        this.flightDataMapper = flightDataMapper;
    }

    public MilesOffer toDomain(RawMilesOfferData rawMilesOfferData){
        if(rawMilesOfferData == null){
            throw new IllegalArgumentException();
        }
        if(rawMilesOfferData.segments() == null){
            throw new IllegalArgumentException();
        }

        List<FlightSegment> segments = new ArrayList<>();
        for(RawFlightData rawSegment : rawMilesOfferData.segments()){
            segments.add(flightDataMapper.toDomain(rawSegment));
        }

        Currency currency = Currency.getInstance(rawMilesOfferData.currencyCode());

        return new MilesOffer(
            new Itinerary(segments), 
            new MilesAmount(Long.parseLong(rawMilesOfferData.milesAmount()), rawMilesOfferData.milesProgram()), 
            new Money(new BigDecimal(rawMilesOfferData.taxesAmount()), currency)
        );
    }
}
