package com.flightoptimizer.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flightoptimizer.domain.Airport;
import com.flightoptimizer.domain.CashOffer;
import com.flightoptimizer.domain.FlightOffer;
import com.flightoptimizer.domain.FlightSegment;
import com.flightoptimizer.domain.Itinerary;
import com.flightoptimizer.domain.MilesOffer;
import com.flightoptimizer.integration.CashOfferDataMapper;
import com.flightoptimizer.integration.FlightDataMapper;
import com.flightoptimizer.integration.JsonRawSearchDataLoader;
import com.flightoptimizer.integration.MilesOfferDataMapper;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JsonOfferDataSourceTest {

    private final Airport gru = new Airport("GRU", "São Paulo");
    private final Airport bsb = new Airport("BSB", "Brasília");
    private final Airport rec = new Airport("REC", "Recife");
    private final Airport cnf = new Airport("CNF", "Belo Horizonte");

    private final FlightDataMapper flightDataMapper =
        new FlightDataMapper();

    private final JsonRawSearchDataLoader loader =
        new JsonRawSearchDataLoader(new ObjectMapper());

    private final CashOfferDataMapper cashOfferDataMapper =
        new CashOfferDataMapper(flightDataMapper);

    private final MilesOfferDataMapper milesOfferDataMapper =
        new MilesOfferDataMapper(flightDataMapper);

    private final JsonOfferDataSource dataSource =
        new JsonOfferDataSource(
            loader,
            cashOfferDataMapper,
            milesOfferDataMapper,
            "sample-search-data.json"
        );

    @Test
    void returnsCashAndMilesOffersForMatchingItineraries() {
        List<Itinerary> itineraries = List.of(
            connectedItinerary(),
            directItinerary()
        );

        List<FlightOffer> result =
            dataSource.findOffers(itineraries);

        assertEquals(4, result.size());

        assertEquals(
            3,
            result.stream()
                .filter(CashOffer.class::isInstance)
                .count()
        );

        assertEquals(
            1,
            result.stream()
                .filter(MilesOffer.class::isInstance)
                .count()
        );

        assertEquals(
            connectedItinerary(),
            result.get(0).itinerary()
        );

        assertEquals(
            directItinerary(),
            result.get(1).itinerary()
        );
    }

    @Test
    void returnsEmptyListWhenNoItineraryMatchesOffers() {
        Itinerary unrelatedItinerary = new Itinerary(
            List.of(
                flight(
                    "AD4000",
                    gru,
                    cnf,
                    8,
                    0,
                    9,
                    20
                )
            )
        );

        List<FlightOffer> result = dataSource.findOffers(
            List.of(unrelatedItinerary)
        );

        assertEquals(List.of(), result);
    }

    @Test
    void rejectsNullItineraries() {
        assertThrows(
            IllegalArgumentException.class,
            () -> dataSource.findOffers(null)
        );
    }

    @Test
    void returnsImmutableOffers() {
        List<FlightOffer> result = dataSource.findOffers(
            List.of(connectedItinerary(), directItinerary())
        );

        assertThrows(
            UnsupportedOperationException.class,
            () -> result.add(result.get(0))
        );
    }

    @Test
    void rejectsNullLoader() {
        assertThrows(
            IllegalArgumentException.class,
            () -> new JsonOfferDataSource(
                null,
                cashOfferDataMapper,
                milesOfferDataMapper,
                "sample-search-data.json"
            )
        );
    }

    @Test
    void rejectsNullCashOfferDataMapper() {
        assertThrows(
            IllegalArgumentException.class,
            () -> new JsonOfferDataSource(
                loader,
                null,
                milesOfferDataMapper,
                "sample-search-data.json"
            )
        );
    }

    @Test
    void rejectsNullMilesOfferDataMapper() {
        assertThrows(
            IllegalArgumentException.class,
            () -> new JsonOfferDataSource(
                loader,
                cashOfferDataMapper,
                null,
                "sample-search-data.json"
            )
        );
    }

    @Test
    void rejectsNullResourceName() {
        assertThrows(
            IllegalArgumentException.class,
            () -> new JsonOfferDataSource(
                loader,
                cashOfferDataMapper,
                milesOfferDataMapper,
                null
            )
        );
    }

    @Test
    void rejectsBlankResourceName() {
        assertThrows(
            IllegalArgumentException.class,
            () -> new JsonOfferDataSource(
                loader,
                cashOfferDataMapper,
                milesOfferDataMapper,
                " "
            )
        );
    }

    private Itinerary connectedItinerary() {
        return new Itinerary(
            List.of(
                flight("LA3000", gru, bsb, 8, 0, 9, 15),
                flight("LA3001", bsb, rec, 10, 15, 12, 30)
            )
        );
    }

    private Itinerary directItinerary() {
        return new Itinerary(
            List.of(
                flight("LA3002", gru, rec, 9, 0, 12, 0)
            )
        );
    }

    private FlightSegment flight(
        String flightNumber,
        Airport origin,
        Airport destination,
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
                2026,
                9,
                10,
                departureHour,
                departureMinute
            ),
            LocalDateTime.of(
                2026,
                9,
                10,
                arrivalHour,
                arrivalMinute
            )
        );
    }
}