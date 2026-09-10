package com.flightoptimizer.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.List;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals; 
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BudgetFilterTest {
    
    private final Airport gru = new Airport("GRU", "São Paulo");
    private final Airport cnf = new Airport("CNF", "Belo Horizonte");
    private final Airport rec = new Airport("REC", "Recife");
    private final Airport bsb = new Airport("BSB", "Brasília");

    private final FlightSegment firstFlight = flight("LA3000", gru, cnf, 10, 00, 11, 00);
    private final FlightSegment secondFlight = flight("LA3001", cnf, rec, 15, 00, 16, 00);
    private final FlightSegment thirdFlight = flight("LA3002", gru, bsb, 10, 00, 11, 00);
    
    private final Itinerary itinerary = new Itinerary(List.of(firstFlight, secondFlight));
    private final Itinerary secondItinerary = new Itinerary(List.of(thirdFlight));

    private final Money fare = new Money(new BigDecimal("1500.00"), Currency.getInstance("BRL"));
    private final Money taxes = new Money(new BigDecimal("249.00"), Currency.getInstance("BRL"));
    private final Money secondTaxes = new Money(new BigDecimal("20.00"), Currency.getInstance("USD"));
    private final Money secondFare = new Money(new BigDecimal("1752.00"), Currency.getInstance("BRL"));
    private final Money thirdFare = new Money(new BigDecimal("200.00"), Currency.getInstance("USD"));
    private final Money expectedM = new Money(new BigDecimal("1749.00"), Currency.getInstance("BRL"));
    private final Money secondExpectedM = new Money(new BigDecimal("2001.00"), Currency.getInstance("BRL"));
    private final Money thirdExpectedM = new Money(new BigDecimal("220.00"), Currency.getInstance("USD"));
    private final Money expectedMi = new Money(new BigDecimal("350.00"), Currency.getInstance("BRL"));
    private final Money firstBudget = new Money(new BigDecimal("2000"), Currency.getInstance("BRL"));
    private final Money secondBudget = new Money(new BigDecimal("1749.00"), Currency.getInstance("BRL"));
    private final MilesAmount firstMilesAmount = new MilesAmount(10_000L, "Latam Pass");

    private final LocalDate departureDate = LocalDate.of(2026, 9, 9);

    private final int maximumSegments = 2;

    private final SearchQuery firstSearchQuery = new SearchQuery(gru, rec, departureDate, maximumSegments, firstBudget);
    private final SearchQuery secondSearchQuery = new SearchQuery(gru, rec, departureDate, maximumSegments, secondBudget);
    private final SearchQuery thirdSearchQuery = new SearchQuery(gru, bsb, departureDate, maximumSegments, firstBudget);

    private final CashOffer firstCashOffer = new CashOffer(itinerary, fare, taxes);
    private final CashOffer secondCashOffer = new CashOffer(secondItinerary, secondFare, taxes);
    private final CashOffer thirdCashOffer = new CashOffer(itinerary, thirdFare, secondTaxes);
    private final MilesOffer firstMilesOffer = new MilesOffer(itinerary, firstMilesAmount, taxes);
    
    private final OfferEvaluation firstOfferEvaluation = new OfferEvaluation(firstCashOffer, expectedM);
    private final OfferEvaluation secondOfferEvaluation = new OfferEvaluation(firstMilesOffer, expectedMi);
    private final OfferEvaluation thirdOfferEvaluation = new OfferEvaluation(secondCashOffer, secondExpectedM);
    private final OfferEvaluation fourthOfferEvaluation = new OfferEvaluation(thirdCashOffer, thirdExpectedM);

    @Test 
    void acceptLowerOfferEvaluation(){
        BudgetFilter budgetFilter= new BudgetFilter();

        assertEquals(
            List.of(firstOfferEvaluation, secondOfferEvaluation), 
            budgetFilter.filter(List.of(firstOfferEvaluation, secondOfferEvaluation), firstSearchQuery.budget())
        );
    }

    @Test 
    void acceptOfferEvaluationEqualToBudget(){
        BudgetFilter budgetFilter = new BudgetFilter();
        assertEquals(
            List.of(firstOfferEvaluation),
            budgetFilter.filter(List.of(firstOfferEvaluation), secondSearchQuery.budget())
        );
    }

    @Test
    void rejectOfferEvaluationAboveBudget(){
        BudgetFilter budgetFilter = new BudgetFilter();
        assertEquals(
            List.of(),
            budgetFilter.filter(List.of(thirdOfferEvaluation), thirdSearchQuery.budget())
        );
    }

    @Test 
    void rejectDifferentCurrencies(){
        BudgetFilter budgetFilter = new BudgetFilter();
        assertThrows(
            IllegalArgumentException.class,
            ()-> budgetFilter.filter(List.of(fourthOfferEvaluation), firstSearchQuery.budget())
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