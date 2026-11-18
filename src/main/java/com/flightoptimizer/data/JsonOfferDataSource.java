package com.flightoptimizer.data;

import java.util.ArrayList;
import java.util.List;

import com.flightoptimizer.domain.Itinerary;
import com.flightoptimizer.domain.MilesOffer;
import com.flightoptimizer.domain.FlightOffer;
import com.flightoptimizer.domain.CashOffer;
import com.flightoptimizer.integration.CashOfferDataMapper;
import com.flightoptimizer.integration.JsonRawSearchDataLoader;
import com.flightoptimizer.integration.MilesOfferDataMapper;
import com.flightoptimizer.integration.RawCashOfferData;
import com.flightoptimizer.integration.RawMilesOfferData;
import com.flightoptimizer.integration.RawSearchData;

public class JsonOfferDataSource implements OfferDataSource{

    private final JsonRawSearchDataLoader loader;
    private final CashOfferDataMapper cashOfferDataMapper;
    private final MilesOfferDataMapper milesOfferDataMapper;
    private final String resourceName;

    public JsonOfferDataSource(
        JsonRawSearchDataLoader loader,
        CashOfferDataMapper cashOfferDataMapper,
        MilesOfferDataMapper milesOfferDataMapper,
        String resourceName
    ){  
        if(loader == null){
            throw new IllegalArgumentException();
        }
        if(cashOfferDataMapper == null){
            throw new IllegalArgumentException();
        }
        if(milesOfferDataMapper== null){
            throw new IllegalArgumentException();
        }
        if(resourceName == null || resourceName.isBlank()){
            throw new IllegalArgumentException();
        }

        this.loader = loader;
        this.cashOfferDataMapper = cashOfferDataMapper;
        this.milesOfferDataMapper = milesOfferDataMapper;
        this.resourceName = resourceName;
    }

    @Override 
    public List<FlightOffer> findOffers(List<Itinerary> itineraries){
        if(itineraries == null){
            throw new IllegalArgumentException();
        }

        RawSearchData searchData = loader.loadFromClasspath(resourceName);
        List<FlightOffer> flightOffers = new ArrayList<>();

        for(RawCashOfferData rawCashOffer : searchData.cashOffers()){
            CashOffer cashOffer = cashOfferDataMapper.toDomain(rawCashOffer);
            
            if(itineraries.contains(cashOffer.itinerary())){
                flightOffers.add(cashOffer);
            }
        }   

        for(RawMilesOfferData rawMilesOffer : searchData.milesOffers()){
            MilesOffer milesOffer = milesOfferDataMapper.toDomain(rawMilesOffer);

            if(itineraries.contains(milesOffer.itinerary())){
                flightOffers.add(milesOffer);
            }
    
        }

        return List.copyOf(flightOffers);
    }
}