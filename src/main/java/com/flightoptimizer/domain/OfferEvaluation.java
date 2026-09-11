package com.flightoptimizer.domain;

public record OfferEvaluation(
    FlightOffer offer,
    Money effectiveCost
){
    public OfferEvaluation{
        if(offer == null){
            throw new IllegalArgumentException("flight offer must not be null");
        }
        if(effectiveCost==null){
            throw new IllegalArgumentException("must not be null");
        }
    }
}