package com.flightoptimizer.domain;

import java.util.Map;

public class OfferEvaluator{

    private final EffectiveCostCalculator calculator;

    public OfferEvaluator(
        EffectiveCostCalculator calculator
    ){
        if(calculator == null){
            throw new IllegalArgumentException("calculator must not be null");
        }

        this.calculator = calculator;
    }

    public OfferEvaluation evaluate(
        FlightOffer offer,
        Map<String, MilesValuation> valuationsByProgram
    ){
        if(offer == null){
            throw new IllegalArgumentException("offer must not be null");
        }
    
        if(offer instanceof CashOffer cashOffer){
            Money effectiveCost = calculator.calculate(cashOffer);
            return new OfferEvaluation(cashOffer, effectiveCost);
        }

        if(offer instanceof MilesOffer milesOffer){
            
            if(valuationsByProgram == null){
                throw new IllegalArgumentException("valuations must not be null");
            }

            MilesValuation valuation = valuationsByProgram.get(milesOffer.miles().program());
            
            if(valuation == null){
                throw new IllegalArgumentException("No valuation found for program" + milesOffer.miles().program());
            }

            Money effectiveCost = calculator.calculate(milesOffer, valuation);
            return new OfferEvaluation(milesOffer, effectiveCost);
        }

        throw new IllegalArgumentException("Unsupported flight offer type" + offer.getClass().getName());
    }
}