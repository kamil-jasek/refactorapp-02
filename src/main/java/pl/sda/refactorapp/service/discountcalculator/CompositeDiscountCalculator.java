package pl.sda.refactorapp.service.discountcalculator;

import static java.util.Objects.requireNonNull;
import static pl.sda.refactorapp.util.ArgumentValidator.check;

import java.util.List;

final class CompositeDiscountCalculator implements DiscountCalculator {

    private final List<DiscountCalculator> calculators;

    CompositeDiscountCalculator(List<DiscountCalculator> calculators) {
        requireNonNull(calculators);
        check(!calculators.isEmpty(), "empty list of calculators");
        this.calculators = calculators;
    }

    @Override
    public void calculate(DiscountContext context) {
        calculators.forEach(calculator -> calculator.calculate(context));
    }
}
