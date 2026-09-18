package com.flightoptimizer.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.List;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class OfferRankerTest {

    private final Airport gru = new Airport("GRU", "São Paulo");
    private final Airport bsb = new Airport("BSB", "Brasília");
    private final Airport cnf = new Airport("CNF","Belo Horizonte");
    private final Airport rec = new Airport("REC","Recife");

    private final FlightSegment firstFlightSegment = flight("LA3000", gru, bsb, 10, 00, 11, 00);
    private final FlightSegment secondFlightSegment = flight("LA3001", bsb, cnf, 12, 00, 13, 00);
    private final FlightSegment thirdFlightSegment = flight("LA3002", cnf, rec, 14, 00, 15, 00); 
    private final FlightSegment fourthFlightSegment = flight("LA3003", bsb, rec, 13, 00, 14, 00);
    private final FlightSegment fifthFlightSegment = flight("LA3004", gru, rec, 10, 00, 12, 00);
    private final FlightSegment sixthFlightSegment = flight("LA3005", gru, rec, 20, 00, 21, 00);
    private final FlightSegment seventhFlightSegment = flight("LA3006", gru, cnf, 11, 00, 12, 00);

    private final Itinerary firstItinerary = new Itinerary(List.of(firstFlightSegment, secondFlightSegment, thirdFlightSegment));
    private final Itinerary secondItinerary = new Itinerary(List.of(firstFlightSegment, fourthFlightSegment));
    private final Itinerary thirdItinerary = new Itinerary(List.of(fifthFlightSegment));
    private final Itinerary fourthItinerary = new Itinerary(List.of(sixthFlightSegment));
    private final Itinerary fifthItinerary = new Itinerary(List.of(seventhFlightSegment, thirdFlightSegment));

    private final Money firstFare = new Money(new BigDecimal("1000.00"), Currency.getInstance("BRL"));
    private final Money secondFare = new Money(new BigDecimal("1200.00"), Currency.getInstance("BRL"));
    private final Money thirdFare = new Money(new BigDecimal("1300.00"), Currency.getInstance("BRL"));
    private final Money taxes = new Money(new BigDecimal("100.00"), Currency.getInstance("BRL"));
    
    private final Money usdTaxes = new Money(new BigDecimal("50.00"), Currency.getInstance("USD"));
    private final Money usdFare = new Money(new BigDecimal("200.00"), Currency.getInstance("USD"));

    private final Money firstEffectiveCost = new Money(new BigDecimal("1100.00"), Currency.getInstance("BRL"));
    private final Money secondEffectiveCost = new Money(new BigDecimal("1300.00"), Currency.getInstance("BRL"));
    private final Money thirdEffectiveCost = new Money(new BigDecimal("1400.00"), Currency.getInstance("BRL"));
    
    private final Money usdEffectiveCost = new Money(new BigDecimal("250.00"), Currency.getInstance("USD"));

    private final CashOffer firstCashOffer = new CashOffer(firstItinerary, firstFare, taxes);
    private final CashOffer secondCashOffer = new CashOffer(secondItinerary, secondFare, taxes);
    private final CashOffer thirdCashOffer = new CashOffer(thirdItinerary, thirdFare, taxes);
    private final CashOffer fourthCashOffer = new CashOffer(fourthItinerary, firstFare, taxes);
    private final CashOffer fifthCashOffer = new CashOffer(fifthItinerary, secondFare, taxes);

    private final CashOffer usdCashOffer = new CashOffer(firstItinerary, usdFare, usdTaxes);

    private final OfferEvaluation firstOfferEvaluation = new OfferEvaluation(firstCashOffer, firstEffectiveCost);
    private final OfferEvaluation secondOfferEvaluation = new OfferEvaluation(secondCashOffer, secondEffectiveCost);
    private final OfferEvaluation thirdOfferEvaluation = new OfferEvaluation(thirdCashOffer, thirdEffectiveCost);
    private final OfferEvaluation fourthOfferEvaluation = new OfferEvaluation(fourthCashOffer, firstEffectiveCost);
    private final OfferEvaluation fifthOfferEvaluation = new OfferEvaluation(fifthCashOffer, secondEffectiveCost);

    private final OfferEvaluation usdOfferEvaluation = new OfferEvaluation(usdCashOffer, usdEffectiveCost);

    @Test 
    void sortOfferEvaluationByLowestCost(){
        OfferRanker ranker = new OfferRanker();
        List<OfferEvaluation> unordered = List.of(thirdOfferEvaluation, firstOfferEvaluation, secondOfferEvaluation);

        assertEquals(
            List.of(firstOfferEvaluation, secondOfferEvaluation, thirdOfferEvaluation),
            ranker.rankByLowestCost(unordered)
        );
    }
    
    @Test 
    void sortOfferByMinSegmentsWhenHaveSameCost(){
        OfferRanker ranker = new OfferRanker();
        List<OfferEvaluation> unordered = List.of(thirdOfferEvaluation, firstOfferEvaluation, secondOfferEvaluation, fourthOfferEvaluation);

        assertEquals(
            List.of(fourthOfferEvaluation, firstOfferEvaluation, secondOfferEvaluation, thirdOfferEvaluation),
            ranker.rankByLowestCost(unordered)
        );
    }

    @Test 
    void sortOfferByLowestCostWhenHaveSameCostAndSegments(){
        OfferRanker ranker = new OfferRanker();
        List<OfferEvaluation> unordered = List.of(firstOfferEvaluation, fifthOfferEvaluation, secondOfferEvaluation, thirdOfferEvaluation, fourthOfferEvaluation);

        assertEquals(
            List.of(fourthOfferEvaluation, firstOfferEvaluation, fifthOfferEvaluation, secondOfferEvaluation, thirdOfferEvaluation),
            ranker.rankByLowestCost(unordered)
        );
    }

    @Test 
    void returnEmptyListWhenRankingLowestPrice(){
        OfferRanker ranker = new OfferRanker();

        assertEquals(
            List.of(),
            ranker.rankByLowestCost(List.of())
        );
    }

    @Test 
    void rejectDifferentCurrenciesLowestPrice(){
        OfferRanker ranker = new OfferRanker();

        assertThrows(
            IllegalArgumentException.class,
            ()-> ranker.rankByLowestCost(List.of(usdOfferEvaluation, firstOfferEvaluation))
        );
    }

    @Test 
    void sortOfferByMinSegments(){
        OfferRanker ranker = new OfferRanker();
        List<OfferEvaluation> unordered = List.of(firstOfferEvaluation, secondOfferEvaluation, thirdOfferEvaluation);

        assertEquals(
            List.of(thirdOfferEvaluation, secondOfferEvaluation, firstOfferEvaluation),
            ranker.rankBalanced(unordered)
        );
    }

    @Test 
    void sortOfferByLowestCostWhenHaveSameNumSegments(){
        OfferRanker ranker = new OfferRanker();
        List<OfferEvaluation> unordered = List.of(firstOfferEvaluation, secondOfferEvaluation, thirdOfferEvaluation, fourthOfferEvaluation);

        assertEquals(
            List.of(fourthOfferEvaluation, thirdOfferEvaluation, secondOfferEvaluation, firstOfferEvaluation),
            ranker.rankBalanced(unordered)
        );
    }

    @Test
    void sortOfferByMinSegmentsWhenHaveSameAndPrice(){
        OfferRanker ranker = new OfferRanker();
        List<OfferEvaluation> unordered = List.of(firstOfferEvaluation, secondOfferEvaluation, fifthOfferEvaluation, thirdOfferEvaluation);

        assertEquals(
            List.of(thirdOfferEvaluation, secondOfferEvaluation, fifthOfferEvaluation, firstOfferEvaluation),
            ranker.rankBalanced(unordered)
        );
    }

    @Test 
    void returnEmptyListWhenMinSegments(){
        OfferRanker ranker = new OfferRanker();

        assertEquals(
            List.of(),
            ranker.rankBalanced(List.of())
        );
    }

    @Test 
    void rejectDifferentCurrenciesMinSegments(){
        OfferRanker ranker = new OfferRanker();

        assertThrows(
            IllegalArgumentException.class,
            ()-> ranker.rankBalanced(List.of(usdOfferEvaluation, firstOfferEvaluation))
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