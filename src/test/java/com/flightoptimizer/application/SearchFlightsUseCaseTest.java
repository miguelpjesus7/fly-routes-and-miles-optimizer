package com.flightoptimizer.application;

import com.flightoptimizer.data.ControlledFlightDataSource;
import com.flightoptimizer.data.ControlledOfferDataSource;
import com.flightoptimizer.data.FlightSourceData;
import com.flightoptimizer.data.OfferDataSource;
import com.flightoptimizer.domain.Airport;
import com.flightoptimizer.domain.BudgetFilter;
import com.flightoptimizer.domain.CashOffer;
import com.flightoptimizer.domain.ConnectionValidator;
import com.flightoptimizer.domain.EffectiveCostCalculator;
import com.flightoptimizer.domain.FlightSegment;
import com.flightoptimizer.domain.Itinerary;
import com.flightoptimizer.domain.MilesAmount;
import com.flightoptimizer.domain.MilesOffer;
import com.flightoptimizer.domain.MilesValuation;
import com.flightoptimizer.domain.Money;
import com.flightoptimizer.domain.OfferEvaluation;
import com.flightoptimizer.domain.OfferEvaluator;
import com.flightoptimizer.domain.OfferRanker;
import com.flightoptimizer.domain.Recommendation;
import com.flightoptimizer.domain.RecommendationService;
import com.flightoptimizer.domain.SearchQuery;
import com.flightoptimizer.domain.SearchQueryValidator;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Currency;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

class SearchFlightsUseCaseTest {

    private final Airport gru = new Airport("GRU", "São Paulo");
    private final Airport bsb = new Airport("BSB", "Brasília");
    private final Airport rec = new Airport("REC", "Recife");

    private final LocalDate searchDate = LocalDate.of(2026, 9, 10);
    private final Clock clock = Clock.fixed(Instant.parse("2026-09-01T12:00:00Z"), ZoneId.of("America/Sao_Paulo"));

    private final Money budget = new Money(new BigDecimal("2000.00"), Currency.getInstance("BRL"));

    private final SearchQuery query = new SearchQuery(gru, rec, searchDate, 2, budget);

    private final FlightSegment gruToBsb = flight("LA3000", gru, bsb, searchDate, 8, 0, 9, 15);
    private final FlightSegment bsbToRec = flight( "LA3001", bsb, rec, searchDate, 10, 15, 12, 30);
    private final FlightSegment gruToRec = flight("LA3002", gru, rec, searchDate, 9, 0, 12, 0);
    private final FlightSegment flightFromAnotherDate = flight("LA3003", gru, rec, searchDate.plusDays(1),10, 0, 13, 0);

    private final Itinerary connectedItinerary = new Itinerary(List.of(gruToBsb, bsbToRec));
    private final Itinerary directItinerary = new Itinerary(List.of(gruToRec));

    private final Money taxes = new Money(new BigDecimal("100.00"), Currency.getInstance("BRL"));

    private final CashOffer cheapConnectedOffer = new CashOffer(connectedItinerary, new Money(new BigDecimal("500.00"), Currency.getInstance("BRL")),taxes);
    private final CashOffer balancedDirectOffer = new CashOffer(directItinerary, new Money(new BigDecimal("1400.00"), Currency.getInstance("BRL")), taxes);
    private final CashOffer aboveBudgetOffer = new CashOffer(directItinerary, new Money(new BigDecimal("2300.00"), Currency.getInstance("BRL")), taxes);
    private final MilesOffer milesOffer = new MilesOffer(directItinerary, new MilesAmount(50_000L, "LATAM Pass"), taxes);

    private final MilesValuation latamPassValuation = new MilesValuation("LATAM Pass", new Money(new BigDecimal("30.00"), Currency.getInstance("BRL")), Instant.parse("2026-09-01T10:00:00Z"));

    private final Map<String, MilesValuation> valuationsByProgram = Map.of("LATAM Pass", latamPassValuation);

    private final FlightSourceData flightDataSource = new ControlledFlightDataSource(List.of(gruToBsb, bsbToRec, gruToRec, flightFromAnotherDate));
    private final OfferDataSource offerDataSource = new ControlledOfferDataSource(List.of(cheapConnectedOffer, balancedDirectOffer, aboveBudgetOffer, milesOffer));

    private final SearchQueryValidator queryValidator = new SearchQueryValidator(clock);
    private final ConnectionValidator connectionValidator = new ConnectionValidator();
    private final OfferEvaluator offerEvaluator = new OfferEvaluator(new EffectiveCostCalculator());
    private final BudgetFilter budgetFilter = new BudgetFilter();
    private final OfferRanker offerRanker = new OfferRanker();
    private final RecommendationService recommendationService = new RecommendationService(offerRanker);

    private final OfferEvaluation expectedCheapestEvaluation = new OfferEvaluation(cheapConnectedOffer, new Money(new BigDecimal("600.00"), Currency.getInstance("BRL")));
    private final OfferEvaluation expectedBalancedEvaluation = new OfferEvaluation(balancedDirectOffer, new Money(new BigDecimal("1500.00"), Currency.getInstance("BRL")));
    private final OfferEvaluation expectedMilesEvaluation = new OfferEvaluation(milesOffer, new Money(new BigDecimal("1600.00"), Currency.getInstance("BRL")));

    private final List<OfferEvaluation> expectedEligibleOffers = List.of(expectedCheapestEvaluation, expectedBalancedEvaluation, expectedMilesEvaluation);
    private final Recommendation expectedRecommendation = new Recommendation(expectedCheapestEvaluation, expectedBalancedEvaluation);
    private final SearchResult expectedSearchResult = new SearchResult(query, expectedEligibleOffers, Optional.of(expectedRecommendation));

    @Test 
    void returnsCompleteSearchResultForViableOffers(){
        SearchFlightsUseCase searchFlightsUseCase= new SearchFlightsUseCase(
            queryValidator,
            flightDataSource,
            offerDataSource,
            connectionValidator,
            offerEvaluator,
            budgetFilter,
            offerRanker,
            recommendationService,
            valuationsByProgram
        );

        assertEquals(
            expectedSearchResult,
            searchFlightsUseCase.execute(query)
        );
    }

    @Test 
    void returnsEmptyResultWhenAllOffersExceedBudget(){
        SearchQuery lowBudgetQuery = new SearchQuery(gru, rec, searchDate, 2, new Money(new BigDecimal("500.00"), Currency.getInstance("BRL")));

        SearchFlightsUseCase searchFlightsUseCase= new SearchFlightsUseCase(
            queryValidator,
            flightDataSource,
            offerDataSource,
            connectionValidator,
            offerEvaluator,
            budgetFilter,
            offerRanker,
            recommendationService,
            valuationsByProgram
        );

        SearchResult expected = new SearchResult(
            lowBudgetQuery,
            List.of(),
            Optional.empty()
        );
        assertEquals(
            expected,
            searchFlightsUseCase.execute(lowBudgetQuery)
        );
    }

    @Test
    void returnsEmptyResultWhenViableItinerariesHaveNoOffers() {
        OfferDataSource noOffersDataSource = new ControlledOfferDataSource(List.of());

        SearchFlightsUseCase searchFlightsUseCase = new SearchFlightsUseCase(
            queryValidator,
            flightDataSource,
            noOffersDataSource,
            connectionValidator,
            offerEvaluator,
            budgetFilter,
            offerRanker,
            recommendationService,
            valuationsByProgram
        );

        SearchResult expected = new SearchResult(
            query,
            List.of(),
            Optional.empty()
        );

        assertEquals(
            expected,
            searchFlightsUseCase.execute(query)
        );
    }

    @Test 
    void rejectsPastSearchQuery(){
        SearchQuery pastDayQuery = new SearchQuery(gru, bsb, LocalDate.of(2026,8,31), 2, budget);

        SearchFlightsUseCase searchFlightsUseCase= new SearchFlightsUseCase(
            queryValidator,
            flightDataSource,
            offerDataSource,
            connectionValidator,
            offerEvaluator,
            budgetFilter,
            offerRanker,
            recommendationService,
            valuationsByProgram
        );

        assertThrows(
            IllegalArgumentException.class,
            ()->  searchFlightsUseCase.execute(pastDayQuery)
        );
    }

    @Test
    void rejectsMilesOfferWithoutMatchingValuation() {
        OfferDataSource offersWithMissingValuation =
            new ControlledOfferDataSource(
                List.of(milesOffer)
        );

        SearchFlightsUseCase searchFlightsUseCase = new SearchFlightsUseCase(
            queryValidator,
            flightDataSource,
            offersWithMissingValuation,
            connectionValidator,
            offerEvaluator,
            budgetFilter,
            offerRanker,
            recommendationService,
            Map.of()
        );

        assertThrows(
            IllegalArgumentException.class,
            () -> searchFlightsUseCase.execute(query)
        );
    }

    private FlightSegment flight(
        String flightNumber,
        Airport origin,
        Airport destination,
        LocalDate date,
        int departureHour,
        int departureMinute,
        int arrivalHour,
        int arrivalMinute
    ) {
        return new FlightSegment(
            flightNumber,
            origin,
            destination,
            LocalDateTime.of(
                date,
                java.time.LocalTime.of(
                    departureHour,
                    departureMinute
                )
            ),
            LocalDateTime.of(
                date,
                java.time.LocalTime.of(
                    arrivalHour,
                    arrivalMinute
                )
            )
        );
    }
}