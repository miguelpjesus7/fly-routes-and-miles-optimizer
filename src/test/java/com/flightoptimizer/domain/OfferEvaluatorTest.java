package com.flightoptimizer.domain;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Currency;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class OfferEvaluatorTest{

    private final Airport gru = new Airport("GRU", "São Paulo");
    private final Airport rec = new Airport("REC", "Recife");

    private final FlightSegment firstFlightSegment = flight("LA3000", gru, rec, 10, 00, 11, 00);
    private final FlightSegment secondFlightSegment = flight("LA3001", gru, rec, 15, 00, 16, 00);

    private final Money fare = new Money(new BigDecimal("700.00"), Currency.getInstance("BRL"));
    private final Money taxes = new Money(new BigDecimal("100.00"), Currency.getInstance("BRL"));
    private final Money firstEffectiveCost = new Money(new BigDecimal("800.00"), Currency.getInstance("BRL"));
    private final Money secondEffectiveCost = new Money(new BigDecimal("450.00"), Currency.getInstance("BRL"));
    private final Money pricePerThousandMiles = new Money(new BigDecimal("35.00"), Currency.getInstance("BRL"));

    private final MilesAmount milesAmount = new MilesAmount(10_000L,"LATAM Pass");

    private final Itinerary firstItinerary = new Itinerary(List.of(firstFlightSegment));
    private final Itinerary secondItinerary = new Itinerary(List.of(secondFlightSegment));

    private final CashOffer cashOffer = new CashOffer(firstItinerary, fare, taxes);
    private final MilesOffer milesOffer = new MilesOffer(secondItinerary, milesAmount, taxes);
    
    private final OfferEvaluation firstOfferEvaluation = new OfferEvaluation(cashOffer, firstEffectiveCost);
    private final OfferEvaluation secondOfferEvaluation = new OfferEvaluation(milesOffer, secondEffectiveCost);

    @Test 
    void evaluatesCashOffer(){
        OfferEvaluator evaluator = new OfferEvaluator(new EffectiveCostCalculator());
        assertEquals(
            firstOfferEvaluation,
            evaluator.evaluate(cashOffer, null)
        );
    }

    @Test 
    void evaluatesMilesOffer(){
        OfferEvaluator evaluator = new OfferEvaluator(new EffectiveCostCalculator());
        Instant observedAt = Instant.now(); 
        Map<String, MilesValuation> valuationsByProgram = new HashMap<String, MilesValuation>()
        {
            {
                put("LATAM Pass", new MilesValuation("LATAM Pass", pricePerThousandMiles, observedAt));
            }
        };

        assertEquals(
            secondOfferEvaluation,
            evaluator.evaluate(milesOffer, valuationsByProgram)
        );
    }

    @Test 
    void rejectNullCalculator(){
        assertThrows(
            IllegalArgumentException.class,
            ()-> new OfferEvaluator(null)
        );
    }

    @Test 
    void rejectNullsOffers(){
        OfferEvaluator evaluator = new OfferEvaluator(new EffectiveCostCalculator());

        assertThrows(
            IllegalArgumentException.class, 
            ()-> evaluator.evaluate(null, null)
        );
    }

    @Test 
    void rejectNullValuationsMap(){
        OfferEvaluator evaluator = new OfferEvaluator(new EffectiveCostCalculator());
        Map<String, MilesValuation> valuationsByProgram = new HashMap<String, MilesValuation>();

        assertThrows(
            IllegalArgumentException.class, 
            ()-> evaluator.evaluate(milesOffer, valuationsByProgram)
        );
    }

    @Test 
    void rejectMilesOfferWithoutValuations(){
        OfferEvaluator evaluator = new OfferEvaluator(new EffectiveCostCalculator());

        assertThrows(
            IllegalArgumentException.class, 
            ()-> evaluator.evaluate(milesOffer, null)
        );
    }

    private FlightSegment flight(
        String flightNumber, Airport origin, Airport destination,
        int departureHour, int departureMinute,
        int arrivalHour, int arrivalMinute) {
        return new FlightSegment(
            flightNumber,
            origin,
            destination,
            LocalDateTime.of(2026, 9, 10, departureHour, departureMinute),
            LocalDateTime.of(2026, 9, 10, arrivalHour, arrivalMinute)
        );
    }
}