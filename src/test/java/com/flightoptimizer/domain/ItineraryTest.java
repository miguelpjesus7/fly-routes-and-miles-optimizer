package com.flightoptimizer.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class ItineraryTest {
    

    @Test 
    void rejectEmptyItinerary(){
        assertThrows(IllegalArgumentException.class, ()-> new Itinerary(List.of()));
    }

    @Test
    void rejectNonConnectSegments(){
        Airport gru = new Airport("GRU", "São Paulo");
        Airport cnf = new Airport("CNF", "Belo Horizonte");
        Airport bsb = new Airport("BSB","Brasília");
        Airport rec = new Airport("REC", "Recife");

        FlightSegment firstFlight = flight("LA3000", gru, cnf, 10, 00, 11, 00);
        FlightSegment secondFlight = flight("LA3001", bsb, rec, 15, 00, 16, 00);
   

        assertThrows(
            IllegalArgumentException.class, 
            ()-> new Itinerary(List.of(firstFlight, secondFlight))
        );
    }

    @Test
    void protectsInternalList() {
        Airport gru = new Airport("GRU", "São Paulo");
        Airport cnf = new Airport("CNF", "Belo Horizonte");
        Airport rec = new Airport("REC", "Recife");

        FlightSegment firstFlight = flight("LA3000", gru, cnf, 10, 0, 11, 0);

        FlightSegment secondFlight = flight("LA3001", cnf, rec, 15, 0, 16, 0);

        List<FlightSegment> sourceSegments = new ArrayList<>();
        sourceSegments.add(firstFlight);

        Itinerary itinerary = new Itinerary(sourceSegments);
        sourceSegments.add(secondFlight);

        assertEquals(
            List.of(firstFlight),
            itinerary.segments()
        );
    }

    @Test 
    void originAndDestinationDerivate(){
        Airport gru = new Airport("GRU", "São Paulo");
        Airport cnf = new Airport("CNF", "Belo Horizonte");
        Airport rec = new Airport("REC", "Recife");

        FlightSegment firstFlight = flight("LA3000", gru, cnf, 10, 00, 11, 00);
        FlightSegment secondFlight = flight("LA3001", cnf, rec, 15, 00, 16, 00);        
        
        Itinerary itinerary = new Itinerary(List.of(firstFlight, secondFlight));

        assertEquals(
            gru,
            itinerary.origin()
        );

        assertEquals(
            rec,
            itinerary.destination()
        );
    }

    @Test
    void rejectTemporalOfInvalidOrder(){
        Airport gru = new Airport("GRU", "São Paulo");
        Airport cnf = new Airport("CNF", "Belo Horizonte");
        Airport rec = new Airport("REC", "Recife");

        FlightSegment firstFlight = flight("LA3000", gru, cnf, 15, 00, 16, 00);
        FlightSegment secondFlight = flight("LA3001", cnf, rec, 10, 00, 11, 00);        

        assertThrows(
            IllegalArgumentException.class,
            ()-> new Itinerary(List.of(firstFlight, secondFlight))
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