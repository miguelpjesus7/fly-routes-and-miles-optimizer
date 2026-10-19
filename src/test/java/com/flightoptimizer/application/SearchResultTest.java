package com.flightoptimizer.application;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.flightoptimizer.domain.Airport;
import com.flightoptimizer.domain.CashOffer;
import com.flightoptimizer.domain.FlightSegment;
import com.flightoptimizer.domain.Itinerary;
import com.flightoptimizer.domain.Money;
import com.flightoptimizer.domain.OfferEvaluation;
import com.flightoptimizer.domain.Recommendation;
import com.flightoptimizer.domain.SearchQuery;

public class SearchResultTest {
    private final Airport gru = new Airport("GRU", "São Paulo");
    private final Airport bsb = new Airport("BSB", "Brasília");
    private final Airport cnf = new Airport("CNF", "Belo Horizonte");
    private final Airport rec = new Airport("REC", "Recife");

    private final LocalDate departureDate = LocalDate.of(2026, 9, 10);

    private final Money budget = new Money( new BigDecimal("2000.00"), Currency.getInstance("BRL"));

    private final SearchQuery query = new SearchQuery( gru, rec, departureDate, 2, budget);

    private final FlightSegment gruToBsb = flight("LA3000", gru, bsb, 10, 0, 11, 30);
    private final FlightSegment bsbToRec = flight("LA3001", bsb, rec, 12, 30, 14, 30);
    private final FlightSegment gruToRec = flight("LA3002", gru, rec, 15, 0, 17, 0);
    private final FlightSegment gruToCnf = flight("LA3003", gru, cnf, 18, 0, 19, 30);

    private final Itinerary connectedItinerary = new Itinerary(List.of(gruToBsb, bsbToRec));
    private final Itinerary directItinerary = new Itinerary(List.of(gruToRec));
    private final Itinerary outsideItinerary = new Itinerary(List.of(gruToCnf));

    private final Money taxes = new Money(new BigDecimal("100.00"),Currency.getInstance("BRL"));

    private final CashOffer connectedOffer = new CashOffer(connectedItinerary,new Money(new BigDecimal("1000.00"), Currency.getInstance("BRL")),taxes);
    private final CashOffer directOffer = new CashOffer(directItinerary, new Money(new BigDecimal("1400.00"), Currency.getInstance("BRL")),taxes);
    private final CashOffer outsideOffer = new CashOffer(outsideItinerary, new Money(new BigDecimal("300.00"), Currency.getInstance("BRL")), taxes);

    private final OfferEvaluation cheapestEvaluation = new OfferEvaluation(connectedOffer, new Money(new BigDecimal("1100.00"), Currency.getInstance("BRL")));
    private final OfferEvaluation balancedEvaluation = new OfferEvaluation(directOffer, new Money(new BigDecimal("1500.00"), Currency.getInstance("BRL")));
    private final OfferEvaluation outsideEvaluation = new OfferEvaluation(outsideOffer, new Money(new BigDecimal("400.00"), Currency.getInstance("BRL")));

    private final List<OfferEvaluation> eligibleOffers = List.of(cheapestEvaluation, balancedEvaluation);

    private final Recommendation validRecommendation = new Recommendation(cheapestEvaluation, balancedEvaluation);
    private final Recommendation invalidRecommendation = new Recommendation(outsideEvaluation, balancedEvaluation);
    
    @Test 
    void createsValidResult(){
        SearchResult result = new SearchResult(query, eligibleOffers, Optional.of(validRecommendation));
        assertEquals(
            query, result.query()
        );
        assertEquals(
            eligibleOffers, result.eligibleOffers()
        );
        assertEquals(
            Optional.of(validRecommendation), result.recommendation()
        );
    }

    @Test 
    void createsEmptyResult(){
        SearchResult result = new SearchResult(query, List.of(), Optional.empty());
        assertEquals(
            query, result.query()
        );
        assertEquals(
            List.of(), result.eligibleOffers()
        );
        assertEquals(
            Optional.empty(), result.recommendation()
        );
    }

    @Test 
    void rejectsNullQuery(){
        assertThrows(
            IllegalArgumentException.class,
            ()-> new SearchResult(null, eligibleOffers, Optional.of(validRecommendation))
        );
    }

    @Test 
    void rejectsNullEligibleOffers(){
        assertThrows(
            IllegalArgumentException.class,
            ()-> new SearchResult(query, null, Optional.of(validRecommendation))
        );
    }

    @Test 
    void rejectsNullRecommendation(){
        assertThrows(
            IllegalArgumentException.class,
            ()-> new SearchResult(query, eligibleOffers, null)
        );
    }

    @Test 
    void rejectsNullOfferInsideEligibleOffers(){
        List<OfferEvaluation> offersWithNull = new ArrayList<>();
        offersWithNull.add(cheapestEvaluation);
        offersWithNull.add(null);
        
        assertThrows(
            IllegalArgumentException.class,
            ()-> new SearchResult(query, offersWithNull, Optional.of(validRecommendation))
        );
    }

    @Test 
    void rejectsEmptyListWithRecommendation(){
        assertThrows(
            IllegalArgumentException.class,
            ()-> new SearchResult(query, List.of(), Optional.of(validRecommendation))
        );
    }

    @Test 
    void rejectsNonEmptyListWithoutRecommendation(){
        assertThrows(
            IllegalArgumentException.class,
            ()-> new SearchResult(query, eligibleOffers, Optional.empty())
        );
    }
    @Test 
    void rejectsRecommendationWithOfferOutsideEligibleOffers(){
        assertThrows(
            IllegalArgumentException.class,
            ()-> new SearchResult(query, eligibleOffers, Optional.of(invalidRecommendation))
        );
    }

    @Test
    void preservesDefensiveCopyOfEligibleOffers() {
        List<OfferEvaluation> mutableOffers = new ArrayList<>(eligibleOffers);
        SearchResult result = new SearchResult(query, mutableOffers, Optional.of(validRecommendation));

        mutableOffers.add(outsideEvaluation);

        assertEquals(
            eligibleOffers,
            result.eligibleOffers()
        );
    }

    @Test
    void returnsImmutableEligibleOffers() {
        SearchResult result = new SearchResult(query, eligibleOffers, Optional.of(validRecommendation));

        assertThrows(
            UnsupportedOperationException.class,
            () -> result.eligibleOffers().add(outsideEvaluation)
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
