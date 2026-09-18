package com.flightoptimizer.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.List;

public class RecommendationTest {

    private final Airport gru = new Airport("GRU", "São Paulo");
    private final Airport bsb = new Airport("BSB", "Brasília");
    private final Airport cnf = new Airport("CNF","Belo Horizonte");
    private final Airport rec = new Airport("REC","Recife");

    private final FlightSegment firstFlightSegment = flight("LA3000", gru, bsb, 10, 00, 11, 00);
    private final FlightSegment secondFlightSegment = flight("LA3001", bsb, cnf, 12, 00, 13, 00);
    private final FlightSegment thirdFlightSegment = flight("LA3002", cnf, rec, 14, 00, 15, 00); 
    private final FlightSegment fourthFlightSegment = flight("LA3003", bsb, rec, 13, 00, 14, 00);

    private final Itinerary firstItinerary = new Itinerary(List.of(firstFlightSegment, secondFlightSegment, thirdFlightSegment));
    private final Itinerary secondItinerary = new Itinerary(List.of(firstFlightSegment, fourthFlightSegment));

    private final Money firstFare = new Money(new BigDecimal("1000.00"), Currency.getInstance("BRL"));
    private final Money secondFare = new Money(new BigDecimal("1200.00"), Currency.getInstance("BRL"));
    private final Money taxes = new Money(new BigDecimal("100.00"), Currency.getInstance("BRL"));

    private final Money firstEffectiveCost = new Money(new BigDecimal("1100.00"), Currency.getInstance("BRL"));
    private final Money secondEffectiveCost = new Money(new BigDecimal("1300.00"), Currency.getInstance("BRL"));

    private final CashOffer firstCashOffer = new CashOffer(firstItinerary, firstFare, taxes);
    private final CashOffer secondCashOffer = new CashOffer(secondItinerary, secondFare, taxes);

    private final OfferEvaluation offerEvaluationCheapest = new OfferEvaluation(firstCashOffer, firstEffectiveCost);
    private final OfferEvaluation offerEvaluationBalanced = new OfferEvaluation(secondCashOffer, secondEffectiveCost);
    
    private final Recommendation recommendation = new Recommendation(offerEvaluationCheapest, offerEvaluationBalanced);
    private final Recommendation sameRecommendation = new Recommendation(offerEvaluationCheapest, offerEvaluationCheapest);
    
    @Test 
    void createValidRecommendation(){
        assertEquals(
            offerEvaluationCheapest, recommendation.cheapest()
        );
        assertEquals(
            offerEvaluationBalanced, recommendation.balanced()
        );
    }

    @Test 
    void createValidBuildWithSameOffer(){
        assertEquals(
            offerEvaluationCheapest, sameRecommendation.cheapest()
        );
        assertEquals(
            offerEvaluationCheapest, sameRecommendation.balanced()
        );
    }

    @Test 
    void rejectNullCheapestRecommendation(){
        assertThrows(
            IllegalArgumentException.class,
            ()-> new Recommendation(null, offerEvaluationBalanced)
        );
    }

    @Test 
    void rejectNullBalancedRecommendation(){
        assertThrows(
            IllegalArgumentException.class,
            ()-> new Recommendation(offerEvaluationCheapest, null)
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
