package com.flightoptimizer.domain;

import java.util.List;
import java.util.ArrayList;

public class BudgetFilter {
    
    
    public List<OfferEvaluation> filter(List<OfferEvaluation> offers, Money budget){
        
        List<OfferEvaluation> result = new ArrayList<>();

        for(OfferEvaluation offer : offers){
            if(!offer.effectiveCost().currency().equals(budget.currency())){
                throw new IllegalArgumentException("Offer and budget must have same currency");
            }

            if(offer.effectiveCost().amount().compareTo(budget.amount()) <= 0){
                result.add(offer);
            }
        }
        return result;
    }
}
