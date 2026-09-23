package com.flightoptimizer.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.List;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class RecommendationServiceTest {
    
    private final Airport gru = new Airport("GRU", "São Paulo");
    private final Airport bsb = new Airport("BSB", "Brasília");
    private final Airport rec = new Airport("REC","Recife");  

    private final FlightSegment firstFlightSegment = flight("LA3000", gru, bsb, 10, 00, 11, 00);
    private final FlightSegment secondFlightSegment = flight("LA3003", bsb, rec, 13, 00, 14, 00);
    private final FlightSegment thirdFlightSegment = flight("LA3009", gru, rec, 15, 00, 16, 00);

    private final Itinerary firstItinerary = new Itinerary(List.of(firstFlightSegment, secondFlightSegment));
    private final Itinerary secondItinerary = new Itinerary(List.of(thirdFlightSegment));

    private final Money firstFare = new Money(new BigDecimal("500.00"), Currency.getInstance("BRL"));
    private final Money secondFare = new Money(new BigDecimal("700.00"), Currency.getInstance("BRL"));
    private final Money taxes = new Money(new BigDecimal("100.00"), Currency.getInstance("BRL"));

    private final Money firstEffectiveCost = new Money(new BigDecimal("600.00"), Currency.getInstance("BRL"));
    private final Money secondEffectiveCost = new Money(new BigDecimal("800.00"), Currency.getInstance("BRL"));


    private final CashOffer firstCashOffer = new CashOffer(firstItinerary, firstFare, taxes);
    private final CashOffer secondCashOffer = new CashOffer(secondItinerary, secondFare, taxes);

    private final OfferEvaluation firstOfferEvaluation = new OfferEvaluation(firstCashOffer, firstEffectiveCost);
    private final OfferEvaluation secondOfferEvaluation = new OfferEvaluation(secondCashOffer, secondEffectiveCost);

    @Test 
    void service(){
        RecommendationService recommendationService = new RecommendationService(new OfferRanker());

        Recommendation recommendation = recommendationService.recommend(List.of(firstOfferEvaluation, secondOfferEvaluation)).orElseThrow();
       
        assertEquals(
            firstOfferEvaluation, recommendation.cheapest()
        );

        assertEquals(
            secondOfferEvaluation, recommendation.balanced()
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
