package com.flightoptimizer.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;

public class CashOfferTest {

    private final Airport gru = new Airport("GRU", "São Paulo");
    private final Airport cnf = new Airport("CNF", "Belo Horizonte");
    private final Airport rec = new Airport("REC", "Recife");

    private final FlightSegment firstFlight = flight("LA3000", gru, cnf, 10, 00, 11, 00);
    private final FlightSegment secondFlight = flight("LA3001", cnf, rec, 15, 00, 16, 00);

    private final Itinerary itinerary = new Itinerary(List.of(firstFlight, secondFlight));

    private final Money fareTest1 = new Money(new BigDecimal("1200"), Currency.getInstance("BRL"));
    private final Money taxes = new Money(new BigDecimal("120"), Currency.getInstance("BRL"));
    private final Money fareTest2 = new Money(new BigDecimal("200"), Currency.getInstance("USD"));

    @Test
    void createValidCashOffer(){
        CashOffer cashOffer = new CashOffer(itinerary, fareTest1, taxes);

        assertEquals(
            fareTest1, cashOffer.fare()
        );
        assertEquals(
            taxes, cashOffer.taxes()
        );
        assertEquals(
            itinerary, cashOffer.itinerary()
        );
    }

    @Test 
    void rejectNullFareOrTaxes(){
        assertThrows(
            IllegalArgumentException.class,
            ()-> new CashOffer(itinerary, null, taxes)
        );
        assertThrows(
            IllegalArgumentException.class,
            ()-> new CashOffer(itinerary, fareTest1, null)
        );
    }

    @Test
    void rejectDifferentCurrency(){
        assertThrows(
            IllegalArgumentException.class,
            ()-> new CashOffer(itinerary, fareTest2, taxes)
        );
    } 

    @Test 
    void rejectNullItinerary(){
        assertThrows(
            IllegalArgumentException.class, 
            ()-> new CashOffer(null, fareTest1, taxes)
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
