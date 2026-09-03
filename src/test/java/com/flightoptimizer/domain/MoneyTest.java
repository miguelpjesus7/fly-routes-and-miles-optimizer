package com.flightoptimizer.domain;

import java.math.BigDecimal;
import java.util.Currency;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MoneyTest {
    
    private final Money brlAmount = new Money(new BigDecimal("1500"), Currency.getInstance("BRL"));
    private final Money zeroAmount = new Money(new BigDecimal("0.00"), Currency.getInstance("BRL"));

    @Test
    void createValidValue(){
        assertEquals(new BigDecimal("1500"), brlAmount.amount());
        assertEquals(Currency.getInstance("BRL"), brlAmount.currency());
    }

    @Test 
    void rejectNullAmount(){
        assertThrows(IllegalArgumentException.class, ()-> new Money(null, Currency.getInstance("BRL")));
    }

    @Test
    void rejectNegativeAmount(){
        assertThrows(IllegalArgumentException.class, ()-> new Money(new BigDecimal("-1.5"), Currency.getInstance("BRL")));
    }

    @Test 
    void acceptZeroMoneyValue(){
        assertEquals(new BigDecimal("0.00"), zeroAmount.amount());
        assertEquals(Currency.getInstance("BRL"), zeroAmount.currency());
    }
}
