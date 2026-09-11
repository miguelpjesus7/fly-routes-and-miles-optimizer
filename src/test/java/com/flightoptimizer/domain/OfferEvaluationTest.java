package com.flightoptimizer.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.List;

import org.junit.jupiter.api.Test; 
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows; 

public class OfferEvaluationTest {

    private final Airport gru = new Airport("GRU", "São Paulo");
    private final Airport cnf = new Airport("CNF", "Belo Horizonte");
    private final Airport rec = new Airport("REC", "Recife");

    private final FlightSegment firstFlight = flight("LA3000", gru, cnf, 10, 00, 11, 00);
    private final FlightSegment secondFlight = flight("LA3001", cnf, rec, 15, 00, 16, 00);
    private final Itinerary itinerary = new Itinerary(List.of(firstFlight, secondFlight));
    private final Money fare = new Money(new BigDecimal("1500.00"), Currency.getInstance("BRL"));
    private final Money taxes = new Money(new BigDecimal("249.00"), Currency.getInstance("BRL"));

    private final Money expectedM = new Money(new BigDecimal("1749.00"), Currency.getInstance("BRL"));
    private final CashOffer cashOffer = new CashOffer(itinerary, fare, taxes);


    @Test 
    void createValidOfferEvaluation(){
        OfferEvaluation evaluation = new OfferEvaluation(cashOffer, expectedM);
        
        assertEquals(
            cashOffer, evaluation.offer()
        );
        assertEquals(
            expectedM, evaluation.effectiveCost()
        );
    }

    @Test 
    void rejectNullOffer(){
        assertThrows(
            IllegalArgumentException.class,
            ()-> new OfferEvaluation(null, expectedM)
        );
    }

    @Test 
    void rejectNullEffectiveCost(){
        assertThrows(
            IllegalArgumentException.class,
            ()-> new OfferEvaluation(cashOffer, null)
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
