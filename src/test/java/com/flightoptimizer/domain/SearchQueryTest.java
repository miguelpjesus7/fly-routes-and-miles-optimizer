package com.flightoptimizer.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.Currency;

public class SearchQueryTest {

    private final Airport gru = new Airport("GRU", "São Paulo");
    private final Airport rec = new Airport("REC", "Recife");

    private final Money budget = new Money(new BigDecimal("3000.00"), Currency.getInstance("BRL"));
    private final Money zeroBudget = new Money(new BigDecimal("0.00"), Currency.getInstance("BRL"));

    private final LocalDate departureDate = LocalDate.of(2026, 9, 30);

    private final int maximumSegments = 2;
    private final int maximumSegmentsZero = 0;
    private final int maximumSegmentsNegative = -1;
    
    @Test
    void createValidSearchQuery(){
        SearchQuery query = new SearchQuery(gru, rec, departureDate, maximumSegments, budget);
        
        assertEquals(
            gru, query.origin()
        );
        assertEquals(
            rec, query.destination()
        );
        assertEquals(
            departureDate, query.departureDate()
        );
        assertEquals(
            maximumSegments, query.maximumSegments()
        );
        assertEquals(
            budget, query.budget()
        );
    }

    @Test 
    void rejectNullOrigin(){
        assertThrows(
            IllegalArgumentException.class,
            ()-> new SearchQuery(null, rec, departureDate, maximumSegments, budget)
        );
    }

    @Test 
    void rejectNullDestination(){
        assertThrows(
            IllegalArgumentException.class,
            ()-> new SearchQuery(gru, null, departureDate, maximumSegments, budget)
        );
    }

    @Test 
    void rejectNullDepartureDate(){
        assertThrows(
            IllegalArgumentException.class,
            ()-> new SearchQuery(gru, rec, null, maximumSegments, budget)
        );
    }

    @Test 
    void rejectNullBudget(){
        assertThrows(
            IllegalArgumentException.class,
            ()-> new SearchQuery(gru, rec, departureDate, maximumSegments, null)
        );
    }

    @Test 
    void rejectSameOriginAndDestination(){
        assertThrows(
            IllegalArgumentException.class,
            ()-> new SearchQuery(rec, rec, departureDate, maximumSegments, budget)
        );
    }

    @Test 
    void rejectZeroMaximumSegments(){
        assertThrows(
            IllegalArgumentException.class,
            ()-> new SearchQuery(gru, rec, departureDate, maximumSegmentsZero, budget)
        );
    }

    @Test 
    void rejectNegativeMaximumSegments(){
        assertThrows(
            IllegalArgumentException.class,
            ()-> new SearchQuery(gru, rec, departureDate, maximumSegmentsNegative, budget)
        );
    }

    @Test 
    void rejectZeroValueForBudget(){
        assertThrows(
            IllegalArgumentException.class,
            ()-> new SearchQuery(gru, rec, departureDate, maximumSegments, zeroBudget)
        );
    }
}
