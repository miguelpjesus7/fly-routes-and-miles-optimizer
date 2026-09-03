package com.flightoptimizer.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Instant;
import java.util.Currency;
import java.util.List; 

import org.junit.jupiter.api.Test;

public class EffectiveCostCalculatorTest{

    private final Airport gru = new Airport("GRU", "São Paulo");
    private final Airport cnf = new Airport("CNF", "Belo Horizonte");
    private final Airport rec = new Airport("REC", "Recife");

    private final FlightSegment firstFlight = flight("LA3000", gru, cnf, 10, 00, 11, 00);
    private final FlightSegment secondFlight = flight("LA3001", cnf, rec, 15, 00, 16, 00);

    private final Instant observedAt = Instant.now();

    private final Money fare = new Money(new BigDecimal("1500.00"), Currency.getInstance("BRL"));
    private final Money taxes = new Money(new BigDecimal("249.00"), Currency.getInstance("BRL"));
    private final Money taxesUSD = new Money(new BigDecimal("50.00"), Currency.getInstance("USD"));
    private final Money pricePerThousandMiles1 = new Money(new BigDecimal("20.00"), Currency.getInstance("BRL"));
    private final Money pricePerThousandMiles2 = new Money(new BigDecimal("15.789"), Currency.getInstance("BRL"));
    
    private final MilesAmount milesAmountLatam = new MilesAmount(15_000L, "LATAM Pass");
    private final MilesAmount milesAmountSmile = new MilesAmount(15_000L, "Smiles");
    private final MilesValuation milesValuation1 = new MilesValuation("LATAM Pass", pricePerThousandMiles1, observedAt);
    private final MilesValuation milesValuation2 = new MilesValuation("LATAM Pass", pricePerThousandMiles2, observedAt);

    private final Money expectedM = new Money(new BigDecimal("1749.00"), Currency.getInstance("BRL"));
    private final Money expectedMi = new Money(new BigDecimal("549.00"), Currency.getInstance("BRL"));
    private final Money expectedThirdCaseApproximation = new Money(new BigDecimal("485.84"), Currency.getInstance("BRL"));

    private final Itinerary itinerary = new Itinerary(List.of(firstFlight, secondFlight));
    private final CashOffer cashOffer = new CashOffer(itinerary, fare, taxes);
   
    private final MilesOffer milesOffer1 = new MilesOffer(itinerary, milesAmountLatam, taxes);
    private final MilesOffer milesOffer2 = new MilesOffer(itinerary, milesAmountSmile, taxes);
    private final MilesOffer milesOffer3 = new MilesOffer(itinerary, milesAmountLatam, taxesUSD);

    @Test 
    void calculatesEffectiveCostForCashOffer(){
        EffectiveCostCalculator calculator = new EffectiveCostCalculator();
        Money actual = calculator.calculate(cashOffer);

        assertEquals(
            expectedM, actual
        );
    }

    @Test 
    void calculatesEffectiveCostForMilesOffer(){
        EffectiveCostCalculator calculator = new EffectiveCostCalculator();
        Money actual = calculator.calculate(milesOffer1, milesValuation1);

        assertEquals(
            expectedMi, actual
        );

    }

    @Test 
    void rejectMilesProgramDifferent(){
        EffectiveCostCalculator calculator = new EffectiveCostCalculator();
        assertThrows(
            IllegalArgumentException.class, 
            ()->  calculator.calculate(milesOffer2, milesValuation1)
        );
    }

    @Test 
    void rejectDifferentCurrencyForTaxesAndMilesValuation(){
        EffectiveCostCalculator calculator = new EffectiveCostCalculator();
        assertThrows(
            IllegalArgumentException.class,
            ()->  calculator.calculate(milesOffer3, milesValuation1)
        );
    }

    @Test 
    void roundsUpThirdDecimalCase(){
        EffectiveCostCalculator calculator = new EffectiveCostCalculator();
        Money actual = calculator.calculate(milesOffer1, milesValuation2);

        assertEquals(
            expectedThirdCaseApproximation, actual
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