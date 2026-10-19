package com.flightoptimizer.application;

import java.util.List;
import java.util.Optional;

import com.flightoptimizer.domain.SearchQuery;
import com.flightoptimizer.domain.Recommendation;
import com.flightoptimizer.domain.OfferEvaluation;

public record SearchResult(
    SearchQuery query,
    List<OfferEvaluation> eligibleOffers,
    Optional<Recommendation> recommendation
) {
    public SearchResult{
        if(query == null){
            throw new IllegalArgumentException("query must not be null");
        }
        if(recommendation == null){
            throw new IllegalArgumentException("recommendation must not be null");
        }
        if(eligibleOffers == null){
            throw new IllegalArgumentException("offer must not be null");
        }

        for(OfferEvaluation offer : eligibleOffers){
            if(offer == null){
                throw new IllegalArgumentException("eligible offers must not contain null");
            }
        }

        eligibleOffers = List.copyOf(eligibleOffers);

        boolean hasEligibleOffers = !eligibleOffers.isEmpty();
        boolean hasRecommendation = recommendation.isPresent();

        if(hasEligibleOffers!=hasRecommendation){
            throw new IllegalArgumentException("recommendation presence must match eligible offers");
        }

        if(recommendation.isPresent()){
            Recommendation selected = recommendation.get();

            if(!eligibleOffers.contains(selected.cheapest())
            || !eligibleOffers.contains(selected.balanced())){
                throw new IllegalArgumentException("recommended offers must belong to eligible offers");
            }
        }
    }
}
