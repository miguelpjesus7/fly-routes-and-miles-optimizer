package com.flightoptimizer.domain;

import java.time.LocalDate;

public record SearchQuery(
    Airport origin,
    Airport destination,
    LocalDate departureDate,
    int maximumSegments,
    Money budget
) {
    public SearchQuery{
        if(origin == null){
            throw new IllegalArgumentException("origin must not be null");
        }
        if(destination == null){
            throw new IllegalArgumentException("destination must not be null");
        }
        if(departureDate == null){
            throw new IllegalArgumentException("date of departure must not be null");
        }
        if(maximumSegments <= 0){
            throw new IllegalArgumentException("number of segments must not be null");
        }
        if(budget == null 
            || budget.amount().signum() <= 0 
        ){
            throw new IllegalArgumentException("budget must not be null or zero");
        }
        if(origin.equals(destination)){
            throw new IllegalArgumentException("origin and destination must not be that same");
        }

    }
}
