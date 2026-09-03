package com.flightoptimizer.domain;

import java.math.BigDecimal;
import java.util.Currency;

public record Money (
    BigDecimal amount,
    Currency currency
) {
    public Money{
        if(amount == null ){
            throw new IllegalArgumentException("Amount must not be null");
        }
        if(currency == null){
            throw new IllegalArgumentException("Currency must not be null");
        }
        if(amount.signum()<0){
            throw new IllegalArgumentException("Amount must not be negative");
        }
    }
}
