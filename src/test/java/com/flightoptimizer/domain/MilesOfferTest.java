package com.flightoptimizer.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.List;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MilesOfferTest {
    private final Airport gru = new Airport("GRU", "São Paulo");
    private final Airport cnf = new Airport("CNF", "Belo Horizonte");
    private final Airport rec = new Airport("REC", "Recife");

    private final FlightSegment firstFlight = flight("LA3000", gru, cnf, 10, 00, 11, 00);
    private final FlightSegment secondFlight = flight("LA3001", cnf, rec, 15, 00, 16, 00);

    private final Itinerary itinerary = new Itinerary(List.of(firstFlight, secondFlight));
    private final MilesAmount miles= new MilesAmount(10_000L,"LATAM Pass");
    private final Money taxes = new Money(new BigDecimal("120"), Currency.getInstance("BRL"));

    @Test 
    void createValidMilesOffer(){
        MilesOffer milesOffer = new MilesOffer(itinerary, miles, taxes);

        assertEquals(
            itinerary, milesOffer.itinerary()
        );
        assertEquals(
            miles, milesOffer.miles()
        );
        assertEquals(
            taxes, milesOffer.taxes()
        );
    }

    @Test 
    void rejectNullItinerary(){
        assertThrows(
            IllegalArgumentException.class,
            ()-> new MilesOffer(null, miles, taxes)
        );
    }

    @Test 
    void rejectNullMilesAmount(){
        assertThrows(
            IllegalArgumentException.class,
            ()-> new MilesOffer(itinerary, null, taxes)
        );
    }

    @Test 
    void rejectNullTaxes(){
        assertThrows(
            IllegalArgumentException.class,
            ()-> new MilesOffer(itinerary, miles, null)
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
