package com.flightoptimizer.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;

public class OfferRanker {

    public List<OfferEvaluation> rankByLowestCost(List<OfferEvaluation> offers){

        if(offers.equals(null)){
            throw new IllegalArgumentException("");
        }
        
        if(offers.isEmpty()){
            return List.of();
        }
        
        for(int i = 0; i<offers.size()-1; i++){
            OfferEvaluation current = offers.get(i);
            OfferEvaluation next = offers.get(i+1);

            if(current.equals(null)){   
                throw new IllegalArgumentException();
            }

            if(!current.effectiveCost().currency().equals(next.effectiveCost().currency())){
                throw new IllegalArgumentException("");
            }
        }

        List<OfferEvaluation> copyOfOffers = new ArrayList<>();
        copyOfOffers.addAll(offers);

        copyOfOffers.sort(
            Comparator.comparing(
                (OfferEvaluation evaluation) -> evaluation.effectiveCost().amount()
            ).thenComparingInt(
                evaluation -> evaluation.offer().itinerary().segments().size()
            )
        );

        return copyOfOffers;
    }

    public List<OfferEvaluation> rankBalanced(List<OfferEvaluation> offers){

        if(offers.equals(null)){
            throw new IllegalArgumentException("");
        }

        if(offers.isEmpty()){
            return List.of();
        }
        
        for(int i = 0; i<offers.size()-1; i++){
            OfferEvaluation current = offers.get(i);
            OfferEvaluation next = offers.get(i+1);

                        if(current.equals(null)){   
                throw new IllegalArgumentException();
            }

            if(!current.effectiveCost().currency().equals(next.effectiveCost().currency())){
                throw new IllegalArgumentException("");
            }
        }
        
        List<OfferEvaluation> copyOfOffers = new ArrayList<>();
        copyOfOffers.addAll(offers);

        copyOfOffers.sort(
        Comparator.comparingInt(
                (OfferEvaluation evaluation) -> evaluation.offer().itinerary().segments().size()
            ).thenComparing(
                evaluation -> evaluation.effectiveCost().amount()
            )
        );
        
        return copyOfOffers;
    }
}