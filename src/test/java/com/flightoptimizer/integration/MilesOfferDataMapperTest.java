package com.flightoptimizer.integration;

import com.flightoptimizer.domain.MilesOffer;
import com.flightoptimizer.domain.Money;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MilesOfferDataMapperTest {

    private final FlightDataMapper flightDataMapper =
        new FlightDataMapper();

    private final MilesOfferDataMapper mapper =
        new MilesOfferDataMapper(flightDataMapper);

    @Test
    void mapsValidRawMilesOfferDataToMilesOffer() {
        RawFlightData gruToBsb = rawFlight(
            "LA3000",
            "GRU",
            "São Paulo",
            "BSB",
            "Brasília",
            "2026-09-10T08:00:00",
            "2026-09-10T09:15:00"
        );

        RawFlightData bsbToRec = rawFlight(
            "LA3001",
            "BSB",
            "Brasília",
            "REC",
            "Recife",
            "2026-09-10T10:15:00",
            "2026-09-10T12:30:00"
        );

        RawMilesOfferData rawMilesOffer = new RawMilesOfferData(
            List.of(gruToBsb, bsbToRec),
            "50000",
            "LATAM Pass",
            "100.00",
            "BRL"
        );

        MilesOffer result = mapper.toDomain(rawMilesOffer);

        assertEquals(2, result.itinerary().segmentsCount());
        assertEquals("GRU", result.itinerary().origin().getIataCode());
        assertEquals("REC", result.itinerary().destination().getIataCode());

        assertEquals(
            "LA3000",
            result.itinerary().segments().get(0).getFlightNumber()
        );

        assertEquals(
            "LA3001",
            result.itinerary().segments().get(1).getFlightNumber()
        );

        assertEquals(50_000L, result.miles().amount());
        assertEquals("LATAM Pass", result.miles().program());

        assertEquals(
            new Money(
                new BigDecimal("100.00"),
                Currency.getInstance("BRL")
            ),
            result.taxes()
        );
    }

    @Test
    void rejectsNullRawMilesOfferData() {
        assertThrows(
            IllegalArgumentException.class,
            () -> mapper.toDomain(null)
        );
    }

    @Test
    void rejectsNullSegments() {
        RawMilesOfferData rawMilesOffer = new RawMilesOfferData(
            null,
            "50000",
            "LATAM Pass",
            "100.00",
            "BRL"
        );

        assertThrows(
            IllegalArgumentException.class,
            () -> mapper.toDomain(rawMilesOffer)
        );
    }

    @Test
    void rejectsInvalidMilesAmount() {
        RawMilesOfferData rawMilesOffer = new RawMilesOfferData(
            List.of(directFlight()),
            "fifty-thousand",
            "LATAM Pass",
            "100.00",
            "BRL"
        );

        assertThrows(
            IllegalArgumentException.class,
            () -> mapper.toDomain(rawMilesOffer)
        );
    }

    @Test
    void rejectsInvalidCurrencyCode() {
        RawMilesOfferData rawMilesOffer = new RawMilesOfferData(
            List.of(directFlight()),
            "50000",
            "LATAM Pass",
            "100.00",
            "INVALID"
        );

        assertThrows(
            IllegalArgumentException.class,
            () -> mapper.toDomain(rawMilesOffer)
        );
    }

    @Test
    void rejectsBlankMilesProgram() {
        RawMilesOfferData rawMilesOffer = new RawMilesOfferData(
            List.of(directFlight()),
            "50000",
            " ",
            "100.00",
            "BRL"
        );

        assertThrows(
            IllegalArgumentException.class,
            () -> mapper.toDomain(rawMilesOffer)
        );
    }

    private RawFlightData directFlight() {
        return rawFlight(
            "LA3002",
            "GRU",
            "São Paulo",
            "REC",
            "Recife",
            "2026-09-10T09:00:00",
            "2026-09-10T12:00:00"
        );
    }

    private RawFlightData rawFlight(
        String flightNumber,
        String originIataCode,
        String originCity,
        String destinationIataCode,
        String destinationCity,
        String departureDateTime,
        String arrivalDateTime
    ) {
        return new RawFlightData(
            flightNumber,
            originIataCode,
            originCity,
            destinationIataCode,
            destinationCity,
            departureDateTime,
            arrivalDateTime
        );
    }
}