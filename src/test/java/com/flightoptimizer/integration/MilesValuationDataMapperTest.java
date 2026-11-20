package com.flightoptimizer.integration;

import com.flightoptimizer.domain.MilesValuation;
import com.flightoptimizer.domain.Money;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MilesValuationDataMapperTest {

    private final MilesValuationDataMapper mapper =
        new MilesValuationDataMapper();

    @Test
    void mapsValidRawMilesValuationData() {
        RawMilesValuationData rawValuation =
            new RawMilesValuationData(
                "LATAM Pass",
                "30.00",
                "BRL",
                "2026-09-01T10:00:00Z"
            );

        MilesValuation result = mapper.toDomain(rawValuation);

        assertEquals("LATAM Pass", result.program());

        assertEquals(
            new Money(
                new BigDecimal("30.00"),
                Currency.getInstance("BRL")
            ),
            result.pricePerThousandMiles()
        );

        assertEquals(
            Instant.parse("2026-09-01T10:00:00Z"),
            result.observedAt()
        );
    }

    @Test
    void rejectsNullRawMilesValuationData() {
        assertThrows(
            IllegalArgumentException.class,
            () -> mapper.toDomain(null)
        );
    }

    @Test
    void rejectsInvalidPricePerThousandMiles() {
        RawMilesValuationData rawValuation =
            new RawMilesValuationData(
                "LATAM Pass",
                "thirty",
                "BRL",
                "2026-09-01T10:00:00Z"
            );

        assertThrows(
            IllegalArgumentException.class,
            () -> mapper.toDomain(rawValuation)
        );
    }

    @Test
    void rejectsInvalidCurrencyCode() {
        RawMilesValuationData rawValuation =
            new RawMilesValuationData(
                "LATAM Pass",
                "30.00",
                "INVALID",
                "2026-09-01T10:00:00Z"
            );

        assertThrows(
            IllegalArgumentException.class,
            () -> mapper.toDomain(rawValuation)
        );
    }

    @Test
    void rejectsInvalidObservedAt() {
        RawMilesValuationData rawValuation =
            new RawMilesValuationData(
                "LATAM Pass",
                "30.00",
                "BRL",
                "not-a-date"
            );

        assertThrows(
            IllegalArgumentException.class,
            () -> mapper.toDomain(rawValuation)
        );
    }

    @Test
    void rejectsNullObservedAt() {
        RawMilesValuationData rawValuation =
            new RawMilesValuationData(
                "LATAM Pass",
                "30.00",
                "BRL",
                null
            );

        assertThrows(
            IllegalArgumentException.class,
            () -> mapper.toDomain(rawValuation)
        );
    }

    @Test
    void rejectsBlankMilesProgram() {
        RawMilesValuationData rawValuation =
            new RawMilesValuationData(
                " ",
                "30.00",
                "BRL",
                "2026-09-01T10:00:00Z"
            );

        assertThrows(
            IllegalArgumentException.class,
            () -> mapper.toDomain(rawValuation)
        );
    }

    @Test
    void rejectsZeroPricePerThousandMiles() {
        RawMilesValuationData rawValuation =
            new RawMilesValuationData(
                "LATAM Pass",
                "0.00",
                "BRL",
                "2026-09-01T10:00:00Z"
            );

        assertThrows(
            IllegalArgumentException.class,
            () -> mapper.toDomain(rawValuation)
        );
    }
}