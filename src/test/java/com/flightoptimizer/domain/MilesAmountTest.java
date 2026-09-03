package com.flightoptimizer.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MilesAmountTest {

    private final MilesAmount validMiles = new MilesAmount(40_000L, "LATAM Pass");
    
    @Test 
    void createValidMilesAmount(){
        assertEquals(40_000L, validMiles.amount());
        assertEquals("LATAM Pass", validMiles.program());
    }

    @Test
    void rejectNegativeMilesAmount(){
        assertThrows(IllegalArgumentException.class, ()-> new MilesAmount(-12000L, "SMILE"));
    }

    @Test 
    void rejectNullMilesProgram(){
        assertThrows(IllegalArgumentException.class, ()-> new MilesAmount(10000L, null));
    }

    @Test
    void rejectBlankMilesProgram(){
        assertThrows(IllegalArgumentException.class, ()-> new MilesAmount(15_000L, ""));
        assertThrows(IllegalArgumentException.class, ()-> new MilesAmount(10_000L, " "));
    }

    @Test 
    void allowsZeroMiles(){
        MilesAmount validMiles2 = new MilesAmount(0L, "Smile");

        assertEquals(0L, validMiles2.amount());
        assertEquals("Smile", validMiles2.program());
    }
}   
