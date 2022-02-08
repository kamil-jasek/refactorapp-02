package pl.sda.refactorapp.service.discountcalculator;

import static java.util.Objects.requireNonNull;

import pl.sda.refactorapp.dao.DiscountCouponsDao;

// -> coupon
class DiscountCouponCalculator implements DiscountCalculator {

    public static final String DISCOUNT_COUPON_PARAM = "discount_coupon_code";
    private final DiscountCouponsDao couponsDao;

    DiscountCouponCalculator(DiscountCouponsDao couponsDao) {
        requireNonNull(couponsDao);
        this.couponsDao = couponsDao;
    }

    @Override
    public void calculate(DiscountContext context) {
        if (!context.getParameters().containsKey(DISCOUNT_COUPON_PARAM)) {
            return;
        }
        final var couponCode = (String) context.getParameters().get(DISCOUNT_COUPON_PARAM);
        couponsDao.findByCode(couponCode)
            .filter(discountCoupon -> !discountCoupon.isUsed())
            .ifPresent(discountCoupon -> context.updateCurrentDiscount(
                Math.max(context.getCurrentDiscount(), discountCoupon.getValue())));
    }
}
