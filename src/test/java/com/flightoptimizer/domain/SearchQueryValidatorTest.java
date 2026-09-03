package com.flightoptimizer.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Currency;

public class SearchQueryValidatorTest {
    private final Airport gru = new Airport("GRU", "São Paulo");
    private final Airport rec = new Airport("REC", "Recife");
    private final Money budget = new Money(new BigDecimal("3000.00"), Currency.getInstance("BRL"));
    private final LocalDate firstDepartureDate = LocalDate.of(2026, 8, 30);
    private final LocalDate secondDepartureDate = LocalDate.of(2026, 9, 2);
    private final LocalDate thirdDepartureDate = LocalDate.of(2026, 10, 10);
    private final int maximumSegments = 2;
    
    private final Clock clock = Clock.fixed(Instant.parse("2026-09-02T12:00:00Z"),ZoneId.of("America/Sao_Paulo"));
    private final SearchQuery firstSearchQuery = new SearchQuery(gru, rec, firstDepartureDate, maximumSegments, budget);
    private final SearchQuery secondSearchQuery = new SearchQuery(gru, rec, secondDepartureDate, maximumSegments, budget);
    private final SearchQuery thirdSearchQuery = new SearchQuery(rec, gru, thirdDepartureDate, maximumSegments, budget);

    @Test 
    void rejectsDepartureDateBeforeCurrentDate(){
        SearchQueryValidator validator = new SearchQueryValidator(clock);
        
        assertThrows(
            IllegalArgumentException.class,
            ()-> validator.validate(firstSearchQuery)
        );
    }

    @Test 
    void acceptsDepartureDateEqualToCurrentDate(){
        SearchQueryValidator validator = new SearchQueryValidator(clock);
        
        assertDoesNotThrow(
            ()-> validator.validate(secondSearchQuery)
        );
    }

    @Test 
    void acceptsFutureDepartureDate(){
        SearchQueryValidator validator = new SearchQueryValidator(clock);
       
        assertDoesNotThrow(
            ()-> validator.validate(thirdSearchQuery)
        );
    }
}
