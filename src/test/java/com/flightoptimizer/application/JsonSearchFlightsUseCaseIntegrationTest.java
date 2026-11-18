package com.flightoptimizer.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flightoptimizer.data.JsonFlightDataSource;
import com.flightoptimizer.data.JsonOfferDataSource;
import com.flightoptimizer.domain.Airport;
import com.flightoptimizer.domain.BudgetFilter;
import com.flightoptimizer.domain.ConnectionValidator;
import com.flightoptimizer.domain.EffectiveCostCalculator;
import com.flightoptimizer.domain.MilesValuation;
import com.flightoptimizer.domain.Money;
import com.flightoptimizer.domain.OfferEvaluation;
import com.flightoptimizer.domain.OfferEvaluator;
import com.flightoptimizer.domain.OfferRanker;
import com.flightoptimizer.domain.Recommendation;
import com.flightoptimizer.domain.RecommendationService;
import com.flightoptimizer.domain.SearchQuery;
import com.flightoptimizer.domain.SearchQueryValidator;
import com.flightoptimizer.integration.CashOfferDataMapper;
import com.flightoptimizer.integration.FlightDataMapper;
import com.flightoptimizer.integration.JsonRawSearchDataLoader;
import com.flightoptimizer.integration.MilesOfferDataMapper;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Currency;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JsonSearchFlightsUseCaseIntegrationTest {

    private static final Currency BRL = Currency.getInstance("BRL");

    private final Airport gru = new Airport("GRU", "São Paulo");
    private final Airport rec = new Airport("REC", "Recife");

    private final LocalDate searchDate =
        LocalDate.of(2026, 9, 10);

    private final Money budget = money("2000.00");

    private final SearchQuery query = new SearchQuery(
        gru,
        rec,
        searchDate,
        2,
        budget
    );

    private final Clock clock = Clock.fixed(
        Instant.parse("2026-09-01T12:00:00Z"),
        ZoneId.of("America/Sao_Paulo")
    );

    @Test
    void executesCompleteSearchUsingJsonDataSources() {
        FlightDataMapper flightDataMapper =
            new FlightDataMapper();

        JsonRawSearchDataLoader loader =
            new JsonRawSearchDataLoader(new ObjectMapper());

        CashOfferDataMapper cashOfferDataMapper =
            new CashOfferDataMapper(flightDataMapper);

        MilesOfferDataMapper milesOfferDataMapper =
            new MilesOfferDataMapper(flightDataMapper);

        JsonFlightDataSource flightDataSource =
            new JsonFlightDataSource(
                loader,
                flightDataMapper,
                "sample-search-data.json"
            );

        JsonOfferDataSource offerDataSource =
            new JsonOfferDataSource(
                loader,
                cashOfferDataMapper,
                milesOfferDataMapper,
                "sample-search-data.json"
            );

        OfferRanker offerRanker = new OfferRanker();

        SearchFlightsUseCase useCase =
            new SearchFlightsUseCase(
                new SearchQueryValidator(clock),
                flightDataSource,
                offerDataSource,
                new ConnectionValidator(),
                new OfferEvaluator(
                    new EffectiveCostCalculator()
                ),
                new BudgetFilter(),
                offerRanker,
                new RecommendationService(offerRanker),
                Map.of(
                    "LATAM Pass",
                    new MilesValuation(
                        "LATAM Pass",
                        money("30.00"),
                        Instant.parse("2026-09-01T10:00:00Z")
                    )
                )
            );

        SearchResult result = useCase.execute(query);

        assertEquals(query, result.query());
        assertEquals(3, result.eligibleOffers().size());

        OfferEvaluation cheapest =
            result.eligibleOffers().get(0);

        OfferEvaluation secondCheapest =
            result.eligibleOffers().get(1);

        OfferEvaluation thirdCheapest =
            result.eligibleOffers().get(2);

        assertEquals(money("600.00"), cheapest.effectiveCost());
        assertEquals(
            2,
            cheapest.offer().itinerary().segmentsCount()
        );

        assertEquals(
            money("1500.00"),
            secondCheapest.effectiveCost()
        );
        assertEquals(
            1,
            secondCheapest.offer().itinerary().segmentsCount()
        );

        assertEquals(
            money("1600.00"),
            thirdCheapest.effectiveCost()
        );

        Recommendation recommendation =
            result.recommendation().orElseThrow();

        assertEquals(cheapest, recommendation.cheapest());
        assertEquals(
            secondCheapest,
            recommendation.balanced()
        );

        assertTrue(
            result.eligibleOffers().stream()
                .flatMap(
                    evaluation -> evaluation.offer()
                        .itinerary()
                        .segments()
                        .stream()
                )
                .noneMatch(
                    segment -> segment.getFlightNumber()
                        .equals("LA3003")
                )
        );
    }

    private Money money(String amount) {
        return new Money(new BigDecimal(amount), BRL);
    }
}