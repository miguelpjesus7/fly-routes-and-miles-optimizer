package com.flightoptimizer.domain;

public record Recommendation(
    OfferEvaluation cheapest,
    OfferEvaluation balanced
) {
    public Recommendation{
        if(cheapest == null){
            throw new IllegalArgumentException();
        }
        if(balanced == null){
            throw new IllegalArgumentException();
        }
    }
}
