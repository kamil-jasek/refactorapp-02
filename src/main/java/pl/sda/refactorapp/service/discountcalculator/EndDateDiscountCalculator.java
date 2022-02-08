package pl.sda.refactorapp.service.discountcalculator;

import static java.time.LocalDateTime.now;

import java.time.LocalDateTime;

// -> endDate
// -> discount
class EndDateDiscountCalculator implements DiscountCalculator {

    public static final String END_DATE_PARAM = "end_date_end_date";
    public static final String DISCOUNT_PARAM = "end_date_discount";

    @Override
    public void calculate(DiscountContext context) {
        final var parameters = context.getParameters();
        if (!parameters.containsKey(END_DATE_PARAM) || !parameters.containsKey(DISCOUNT_PARAM)) {
            return;
        }
        final var endDate = (LocalDateTime) parameters.get(END_DATE_PARAM);
        final var discount = (Float) parameters.get(DISCOUNT_PARAM);
        if (context.getCurrentDiscount() == 0 && now().isBefore(endDate)) {
            context.updateCurrentDiscount(discount);
        }
    }
}
