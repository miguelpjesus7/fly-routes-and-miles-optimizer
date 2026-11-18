package com.flightoptimizer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flightoptimizer.application.SearchFlightsUseCase;
import com.flightoptimizer.application.SearchResult;
import com.flightoptimizer.data.JsonFlightDataSource;
import com.flightoptimizer.data.JsonOfferDataSource;
import com.flightoptimizer.domain.Airport;
import com.flightoptimizer.domain.BudgetFilter;
import com.flightoptimizer.domain.CashOffer;
import com.flightoptimizer.domain.ConnectionValidator;
import com.flightoptimizer.domain.EffectiveCostCalculator;
import com.flightoptimizer.domain.FlightOffer;
import com.flightoptimizer.domain.FlightSegment;
import com.flightoptimizer.domain.Itinerary;
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
import com.flightoptimizer.integration.CashOfferDataMapper;
import com.flightoptimizer.integration.FlightDataMapper;
import com.flightoptimizer.integration.JsonRawSearchDataLoader;
import com.flightoptimizer.integration.MilesOfferDataMapper;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Currency;
import java.util.Locale;
import java.util.Map;

public class Main {

    private static final Currency BRL = Currency.getInstance("BRL");

    private static final NumberFormat MONEY_FORMAT =
        NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

    private static final DateTimeFormatter TIME_FORMAT =
        DateTimeFormatter.ofPattern("HH:mm");

    public static void main(String[] args) {
        Airport gru = new Airport("GRU", "São Paulo");
        Airport rec = new Airport("REC", "Recife");

        SearchQuery query = new SearchQuery(
            gru,
            rec,
            LocalDate.of(2026, 9, 10),
            2,
            money("2000.00")
        );

        Clock clock = Clock.fixed(
            Instant.parse("2026-09-01T12:00:00Z"),
            ZoneId.of("America/Sao_Paulo")
        );

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

        printResult(result);
    }

    private static void printResult(SearchResult result) {
        SearchQuery query = result.query();

        System.out.println("=== Fly Routes and Miles Optimizer ===");
        System.out.println(
            "Busca: "
                + query.origin().getIataCode()
                + " -> "
                + query.destination().getIataCode()
                + " | Data: "
                + query.departureDate()
        );
        System.out.println("Orçamento: " + formatMoney(query.budget()));
        System.out.println();

        if (result.eligibleOffers().isEmpty()) {
            System.out.println("Nenhuma oferta elegível foi encontrada.");
            return;
        }

        System.out.println("Ofertas elegíveis:");

        for (int i = 0; i < result.eligibleOffers().size(); i++) {
            OfferEvaluation evaluation = result.eligibleOffers().get(i);

            System.out.println();
            System.out.println(
                (i + 1)
                    + ". Custo efetivo: "
                    + formatMoney(evaluation.effectiveCost())
            );

            printOffer(evaluation.offer());
        }

        Recommendation recommendation =
            result.recommendation().orElseThrow();

        System.out.println();
        System.out.println("=== Recomendações ===");
        System.out.println(
            "Mais barata: "
                + formatMoney(
                    recommendation.cheapest().effectiveCost()
                )
        );
        System.out.println(
            "Mais equilibrada: "
                + formatMoney(
                    recommendation.balanced().effectiveCost()
                )
        );
    }

    private static void printOffer(FlightOffer offer) {
        Itinerary itinerary = offer.itinerary();

        System.out.println(
            "   Itinerário: "
                + itinerary.origin().getIataCode()
                + " -> "
                + itinerary.destination().getIataCode()
        );

        System.out.println(
            "   Segmentos: "
                + itinerary.segmentsCount()
                + " | Conexões: "
                + itinerary.connectionCount()
                + " | Duração: "
                + formatDuration(itinerary.totalDuration())
        );

        for (FlightSegment segment : itinerary.segments()) {
            System.out.println(
                "   - "
                    + segment.getFlightNumber()
                    + ": "
                    + segment.getOrigin().getIataCode()
                    + " -> "
                    + segment.getDestination().getIataCode()
                    + " ("
                    + segment.getDepartureTime().format(TIME_FORMAT)
                    + " - "
                    + segment.getArrivalTime().format(TIME_FORMAT)
                    + ")"
            );
        }

        if (offer instanceof CashOffer cashOffer) {
            System.out.println(
                "   Pagamento: dinheiro | Tarifa: "
                    + formatMoney(cashOffer.fare())
                    + " | Taxas: "
                    + formatMoney(cashOffer.taxes())
            );
        }

        if (offer instanceof MilesOffer milesOffer) {
            System.out.println(
                "   Pagamento: "
                    + milesOffer.miles().amount()
                    + " milhas "
                    + milesOffer.miles().program()
                    + " | Taxas: "
                    + formatMoney(milesOffer.taxes())
            );
        }
    }

    private static Money money(String amount) {
        return new Money(new BigDecimal(amount), BRL);
    }

    private static String formatMoney(Money money) {
        MONEY_FORMAT.setCurrency(money.currency());
        return MONEY_FORMAT.format(money.amount());
    }

    private static String formatDuration(Duration duration) {
        return duration.toHours()
            + "h "
            + duration.toMinutesPart()
            + "min";
    }
}