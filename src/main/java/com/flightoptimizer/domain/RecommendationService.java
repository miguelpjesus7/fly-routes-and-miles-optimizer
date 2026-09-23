package com.flightoptimizer.domain;

import java.util.Optional;
import java.util.List;

public class RecommendationService {

    private final OfferRanker ranker;

    public RecommendationService(OfferRanker ranker){
        if(ranker == null){
            throw new IllegalArgumentException("ranker must not be null");
        }
        this.ranker = ranker;
    }
    
    Optional<Recommendation> recommend(List<OfferEvaluation> eligibleOffers){
        if(eligibleOffers.isEmpty()){
            return Optional.empty();
        }

        List<OfferEvaluation> cheapest = ranker.rankByLowestCost(eligibleOffers);
        List<OfferEvaluation> balanced = ranker.rankBalanced(eligibleOffers);

        return Optional.of(new Recommendation(cheapest.get(0), balanced.get(0)));
    }
}
