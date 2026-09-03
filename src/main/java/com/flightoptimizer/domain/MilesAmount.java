package com.flightoptimizer.domain;

public record MilesAmount(
    long amount,
    String program
){
    public MilesAmount{
        if(amount<0){
            throw new IllegalArgumentException("Miles amount must not be negative");
        }

        if(program == null || program.isBlank()){
            throw new IllegalArgumentException("Miles program must not be blank");
        }
    }
}
