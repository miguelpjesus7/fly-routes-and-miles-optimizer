package com.flightoptimizer.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class EffectiveCostCalculator {
    public Money calculate(CashOffer cashOffer){
        BigDecimal sum = cashOffer.fare().amount().add(cashOffer.taxes().amount());
        BigDecimal roundedSum = sum.setScale(2, RoundingMode.CEILING);

        return new Money(roundedSum, cashOffer.fare().currency()); 
    }

    public Money calculate(MilesOffer milesOffer, MilesValuation milesValuation){
        if(!milesOffer.miles().program().equals(milesValuation.program())){
            throw new IllegalArgumentException();
        }
        if(!milesOffer.taxes().currency().equals(milesValuation.pricePerThousandMiles().currency())){
            throw new IllegalArgumentException();
        }
        BigDecimal thousandOfMiles = BigDecimal.valueOf(milesOffer.miles().amount()).divide(BigDecimal.valueOf(1000));
        BigDecimal milesInMoney = milesValuation.pricePerThousandMiles().amount().multiply(thousandOfMiles);
        BigDecimal milesInMoneyPlusTaxes = milesInMoney.add(milesOffer.taxes().amount());
        BigDecimal roundedMilesInMoneyPlusTaxes = milesInMoneyPlusTaxes.setScale(2, RoundingMode.CEILING);

        return new Money(roundedMilesInMoneyPlusTaxes, milesValuation.pricePerThousandMiles().currency());
    }
}
