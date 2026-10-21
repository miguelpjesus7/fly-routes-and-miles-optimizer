package com.flightoptimizer;

import com.flightoptimizer.application.SearchFlightsUseCase;
import com.flightoptimizer.application.SearchResult;
import com.flightoptimizer.data.ControlledFlightDataSource;
import com.flightoptimizer.data.ControlledOfferDataSource;
import com.flightoptimizer.domain.Airport;
import com.flightoptimizer.domain.BudgetFilter;
import com.flightoptimizer.domain.CashOffer;
import com.flightoptimizer.domain.ConnectionValidator;
import com.flightoptimizer.domain.EffectiveCostCalculator;
import com.flightoptimizer.domain.FlightOffer;
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
import java.text.NumberFormat;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Currency;
import java.util.List;
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
        Airport bsb = new Airport("BSB", "Brasília");
        Airport rec = new Airport("REC", "Recife");

        LocalDate searchDate = LocalDate.of(2026, 9, 10);

        FlightSegment gruToBsb = flight(
            "LA3000", gru, bsb, searchDate, 8, 0, 9, 15
        );
        FlightSegment bsbToRec = flight(
            "LA3001", bsb, rec, searchDate, 10, 15, 12, 30
        );
        FlightSegment gruToRec = flight(
            "LA3002", gru, rec, searchDate, 9, 0, 12, 0
        );
        FlightSegment flightFromAnotherDate = flight(
            "LA3003", gru, rec, searchDate.plusDays(1), 10, 0, 13, 0
        );

        Itinerary connectedItinerary =
            new Itinerary(List.of(gruToBsb, bsbToRec));
        Itinerary directItinerary =
            new Itinerary(List.of(gruToRec));

        Money taxes = money("100.00");
        CashOffer cheapConnectedOffer =
            new CashOffer(connectedItinerary, money("500.00"), taxes);
        CashOffer balancedDirectOffer =
            new CashOffer(directItinerary, money("1400.00"), taxes);
        CashOffer aboveBudgetOffer =
            new CashOffer(directItinerary, money("2300.00"), taxes);

        MilesOffer milesOffer = new MilesOffer(
            directItinerary,
            new MilesAmount(50_000L, "LATAM Pass"),
            taxes
        );

        MilesValuation latamPassValuation = new MilesValuation(
            "LATAM Pass",
            money("30.00"),
            Instant.parse("2026-09-01T10:00:00Z")
        );

        SearchQuery query = new SearchQuery(
            gru,
            rec,
            searchDate,
            2,
            money("2000.00")
        );

        Clock clock = Clock.fixed(
            Instant.parse("2026-09-01T12:00:00Z"),
            ZoneId.of("America/Sao_Paulo")
        );

        SearchFlightsUseCase searchFlightsUseCase =
            new SearchFlightsUseCase(
                new SearchQueryValidator(clock),
                new ControlledFlightDataSource(List.of(
                    gruToBsb,
                    bsbToRec,
                    gruToRec,
                    flightFromAnotherDate
                )),
                new ControlledOfferDataSource(List.of(
                    cheapConnectedOffer,
                    balancedDirectOffer,
                    aboveBudgetOffer,
                    milesOffer
                )),
                new ConnectionValidator(),
                new OfferEvaluator(new EffectiveCostCalculator()),
                new BudgetFilter(),
                new OfferRanker(),
                new RecommendationService(new OfferRanker()),
                Map.of("LATAM Pass", latamPassValuation)
            );

        SearchResult result = searchFlightsUseCase.execute(query);

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
            System.out.println((i + 1) + ". Custo efetivo: "
                + formatMoney(evaluation.effectiveCost()));

            printOffer(evaluation.offer());
        }

        Recommendation recommendation = result.recommendation().orElseThrow();

        System.out.println();
        System.out.println("=== Recomendações ===");
        System.out.println("Mais barata: "
            + formatMoney(recommendation.cheapest().effectiveCost()));
        System.out.println("Mais equilibrada: "
            + formatMoney(recommendation.balanced().effectiveCost()));
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
            "   Segmentos: " + itinerary.segmentsCount()
                + " | Conexões: " + itinerary.connectionCount()
                + " | Duração: " + formatDuration(itinerary.totalDuration())
        );

        for (FlightSegment segment : itinerary.segments()) {
            System.out.println(
                "   - " + segment.getFlightNumber()
                    + ": " + segment.getOrigin().getIataCode()
                    + " -> " + segment.getDestination().getIataCode()
                    + " (" + segment.getDepartureTime().format(TIME_FORMAT)
                    + " - " + segment.getArrivalTime().format(TIME_FORMAT)
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

    private static FlightSegment flight(
        String number,
        Airport origin,
        Airport destination,
        LocalDate date,
        int departureHour,
        int departureMinute,
        int arrivalHour,
        int arrivalMinute
    ) {
        return new FlightSegment(
            number,
            origin,
            destination,
            LocalDateTime.of(date, java.time.LocalTime.of(
                departureHour, departureMinute
            )),
            LocalDateTime.of(date, java.time.LocalTime.of(
                arrivalHour, arrivalMinute
            ))
        );
    }

    private static Money money(String amount) {
        return new Money(new BigDecimal(amount), BRL);
    }

    private static String formatMoney(Money money) {
        MONEY_FORMAT.setCurrency(money.currency());
        return MONEY_FORMAT.format(money.amount());
    }

    private static String formatDuration(Duration duration) {
        long hours = duration.toHours();
        long minutes = duration.toMinutesPart();

        return hours + "h " + minutes + "min";
    }
}