package com.flightoptimizer.domain;

import java.math.BigDecimal;
import java.util.Currency;
import java.time.Instant;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MilesValuationTest {
    private final Money firstMoney = new Money(new BigDecimal("15.50"), Currency.getInstance("BRL")); 
    private final Money zeroMoney = new Money(new BigDecimal("0"), Currency.getInstance("BRL"));

    private final Instant observedAt = Instant.now();

    private final MilesValuation firstMilesValuation = new MilesValuation("LATAM Pass", firstMoney, observedAt);

    @Test 
    void createValidMilesValuation(){
        assertEquals(
            "LATAM Pass", firstMilesValuation.program()
        );
        assertEquals(
            firstMoney, firstMilesValuation.pricePerThousandMiles()
        );
        assertEquals(
            observedAt, firstMilesValuation.observedAt()
        );
    }

    @Test 
    void rejectNullProgram(){
        assertThrows(
            IllegalArgumentException.class,
            ()-> new MilesValuation(null, firstMoney, observedAt)
        );
    }

    @Test 
    void rejectEmptyProgram(){
        assertThrows(
            IllegalArgumentException.class,
            ()-> new MilesValuation(" ", firstMoney, observedAt)
        );
        assertThrows(
            IllegalArgumentException.class,
            ()-> new MilesValuation("", firstMoney, observedAt)
        );
    }

    @Test 
    void rejectNullPricePerThousandMiles(){
        assertThrows(
            IllegalArgumentException.class,
            ()-> new MilesValuation("Smiles", null, observedAt)
        );
    }

    @Test 
    void rejectNullObservedAt(){
        assertThrows(
            IllegalArgumentException.class,
            ()-> new MilesValuation("Smiles", firstMoney, null)
        );
    }

    @Test 
    void rejectZeroMoneyValueForPricePerThounsandMiles(){
        assertThrows(
            IllegalArgumentException.class,
            ()-> new MilesValuation("LATAM Pass", zeroMoney, observedAt)
        );
    }
}
