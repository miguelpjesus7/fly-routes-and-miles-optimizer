package com.flightoptimizer.data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.List;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.flightoptimizer.domain.Airport;
import com.flightoptimizer.domain.CashOffer;
import com.flightoptimizer.domain.FlightOffer;
import com.flightoptimizer.domain.FlightSegment;
import com.flightoptimizer.domain.Itinerary;
import com.flightoptimizer.domain.MilesAmount;
import com.flightoptimizer.domain.MilesOffer;
import com.flightoptimizer.domain.Money;

public class ControlledOfferDataSourceTest{
    
    private final Airport gru = new Airport("GRU", "São Paulo");
    private final Airport bsb = new Airport("BSB", "Brasília");
    private final Airport cnf = new Airport("CNF","Belo Horizonte");
    private final Airport rec = new Airport("REC","Recife");
    private final Airport mcz = new Airport("MCZ", "Maceió");
    private final Airport vcp = new Airport("VCP", "Campinas");

    private final FlightSegment firstFlightSegment = flight("LA3000", gru, bsb, 10, 00, 11, 00);
    private final FlightSegment secondFlightSegment = flight("LA3001", bsb, cnf, 12, 00, 13, 00);
    private final FlightSegment thirdFlightSegment = flight("LA3002", cnf, rec, 14, 00, 15, 00); 
    private final FlightSegment fourthFlightSegment = flight("LA3003", bsb, rec, 13, 00, 14, 00);
    private final FlightSegment fifthFlightSegment = flight("LA3021", mcz, vcp, 10, 00, 12, 00);


    private final Itinerary firstItinerary = new Itinerary(List.of(firstFlightSegment, secondFlightSegment, thirdFlightSegment));
    private final Itinerary secondItinerary = new Itinerary(List.of(firstFlightSegment, fourthFlightSegment));
    private final Itinerary thirdItinerary = new Itinerary(List.of(thirdFlightSegment));
    private final Itinerary fourthItinerary = new Itinerary(List.of(fifthFlightSegment));

    private final Money firstFare = new Money(new BigDecimal("1000.00"), Currency.getInstance("BRL"));
    private final Money secondFare = new Money(new BigDecimal("1200.00"), Currency.getInstance("BRL"));
    private final Money taxes = new Money(new BigDecimal("100.00"), Currency.getInstance("BRL"));

    private final MilesAmount milesAmount = new MilesAmount(10_000L, "LATAM Pass");

    private final CashOffer firstCashOffer = new CashOffer(firstItinerary, firstFare, taxes);
    private final CashOffer secondCashOffer = new CashOffer(secondItinerary, secondFare, taxes);
    private final CashOffer thirdCashOffer = new CashOffer(thirdItinerary, secondFare, taxes);
    
    private final MilesOffer firstMilesOffer = new MilesOffer(firstItinerary, milesAmount, taxes);

    @Test 
    void itineraryReturnOffer(){
        ControlledOfferDataSource offerDataSource = new ControlledOfferDataSource(List.of(firstCashOffer, thirdCashOffer));
        assertEquals(
            List.of(firstCashOffer),
            offerDataSource.findOffers(List.of(firstItinerary))
           
        );
    }

    @Test 
    void itineraryMissMatchNotReturnAnOffer(){
        ControlledOfferDataSource offerDataSource = new ControlledOfferDataSource(List.of(firstCashOffer, secondCashOffer, thirdCashOffer));
        assertEquals(
            List.of(),
            offerDataSource.findOffers(List.of(fourthItinerary))
        );
    }

    @Test 
    void returnTwoOfferForSameItinerary(){
        ControlledOfferDataSource offerDataSource = new ControlledOfferDataSource(List.of(firstCashOffer, secondCashOffer, thirdCashOffer, firstMilesOffer));
        assertEquals(
            List.of(firstCashOffer, firstMilesOffer),
            offerDataSource.findOffers(List.of(firstItinerary))
        );
    }

    @Test 
    void itineraryEmptyListReturnEmptyList(){
        ControlledOfferDataSource offerDataSource = new ControlledOfferDataSource(List.of(firstCashOffer, secondCashOffer, thirdCashOffer));
        assertEquals(
            List.of(),
            offerDataSource.findOffers(List.of())
        );
    }

    @Test 
    void returnImmutableResult(){
        ControlledOfferDataSource offerDataSource = new ControlledOfferDataSource(List.of(firstCashOffer, secondCashOffer));
        List<FlightOffer> result = offerDataSource.findOffers(List.of(firstItinerary));
        
        assertThrows(
        UnsupportedOperationException.class,
        () -> result.add(secondCashOffer)
        );

    }

    @Test 
    void rejectNullAvailableOffers(){
        assertThrows(
            IllegalArgumentException.class,
            ()-> new ControlledOfferDataSource(null)
        );
    }

    @Test 
    void rejectNullItineraries(){
        ControlledOfferDataSource offerDataSource = new ControlledOfferDataSource(List.of(firstCashOffer, secondCashOffer));
        assertThrows(
            IllegalArgumentException.class,
            ()-> offerDataSource.findOffers(null)
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