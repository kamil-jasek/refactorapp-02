package pl.sda.refactorapp.service.discountcalculator;

import static java.time.LocalDateTime.now;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.sda.refactorapp.dao.DiscountCouponsDao;
import pl.sda.refactorapp.entity.DiscountCoupon;
import pl.sda.refactorapp.entity.Item;

@ExtendWith(MockitoExtension.class)
class DiscountCalculatorTest {

    @Mock
    private DiscountCouponsDao couponsDao;

    @Test
    void shouldCalculateDiscount() {
        // given
        final var calculator = new CompositeDiscountCalculator(List.of(
            new EndDateDiscountCalculator(),
            new DiscountCouponCalculator(couponsDao)));

        final var context = new DiscountContext(randomUUID(),
            List.of(new Item()),
            Map.of(
                "discount_coupon_code", "ABC200",
                "end_date_end_date", now().plus(1, DAYS),
                "end_date_discount", 0.2f),
            1);

        // when
        calculator.calculate(context);

        // then
        assertEquals(0.2f, context.getCurrentDiscount());
    }

    @Test
    void shouldCalculateDiscountByCoupon() {
        // given
        final var calculator = new CompositeDiscountCalculator(List.of(
            new EndDateDiscountCalculator(),
            new DiscountCouponCalculator(couponsDao)
        ));

        final var discountCoupon = new DiscountCoupon();
        discountCoupon.setValue(0.4f);
        given(couponsDao.findByCode("ABC200")).willReturn(Optional.of(discountCoupon));

        final var context = new DiscountContext(randomUUID(),
            List.of(new Item()),
            Map.of(
                "discount_coupon_code", "ABC200",
                "end_date_end_date", now().plus(1, DAYS),
                "end_date_discount", 0.2f),
            1);

        // when
        calculator.calculate(context);

        // then
        assertEquals(0.4f, context.getCurrentDiscount());
    }
}