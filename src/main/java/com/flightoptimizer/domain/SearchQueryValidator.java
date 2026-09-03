package com.flightoptimizer.domain;

import java.time.Clock;
import java.time.LocalDate;

public class SearchQueryValidator {

    private final Clock clock;

    public SearchQueryValidator(
        Clock clock
    ){
        if(clock == null){
            throw new IllegalArgumentException("clock must not be null");
        }
        this.clock = clock;
    }

    public void validate(SearchQuery searchQuery){
        LocalDate today = LocalDate.now(this.clock);
        
        if(searchQuery.departureDate().isBefore(today)){
            throw new IllegalArgumentException();
        }
    }
}