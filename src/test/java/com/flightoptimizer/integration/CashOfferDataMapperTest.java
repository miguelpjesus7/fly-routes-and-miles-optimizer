package com.flightoptimizer.integration;

import com.flightoptimizer.domain.CashOffer;
import com.flightoptimizer.domain.Money;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CashOfferDataMapperTest {

    private final FlightDataMapper flightDataMapper =
        new FlightDataMapper();

    private final CashOfferDataMapper mapper =
        new CashOfferDataMapper(flightDataMapper);

    @Test
    void mapsValidRawCashOfferDataToCashOffer() {
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

        RawCashOfferData rawCashOffer = new RawCashOfferData(
            List.of(gruToBsb, bsbToRec),
            "500.00",
            "100.00",
            "BRL"
        );

        CashOffer result = mapper.toDomain(rawCashOffer);

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

        assertEquals(
            new Money(
                new BigDecimal("500.00"),
                Currency.getInstance("BRL")
            ),
            result.fare()
        );

        assertEquals(
            new Money(
                new BigDecimal("100.00"),
                Currency.getInstance("BRL")
            ),
            result.taxes()
        );
    }

    @Test
    void rejectsNullRawCashOfferData() {
        assertThrows(
            IllegalArgumentException.class,
            () -> mapper.toDomain(null)
        );
    }

    @Test
    void rejectsNullSegments() {
        RawCashOfferData rawCashOffer = new RawCashOfferData(
            null,
            "500.00",
            "100.00",
            "BRL"
        );

        assertThrows(
            IllegalArgumentException.class,
            () -> mapper.toDomain(rawCashOffer)
        );
    }

    @Test
    void rejectsInvalidCurrencyCode() {
        RawCashOfferData rawCashOffer = new RawCashOfferData(
            List.of(directFlight()),
            "500.00",
            "100.00",
            "INVALID"
        );

        assertThrows(
            IllegalArgumentException.class,
            () -> mapper.toDomain(rawCashOffer)
        );
    }

    @Test
    void rejectsNullFlightDataMapper() {
        assertThrows(
            IllegalArgumentException.class,
            () -> new CashOfferDataMapper(null)
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