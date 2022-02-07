package pl.sda.refactorapp.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import pl.sda.refactorapp.entity.Item;

public final class MakeOrderForm {

    private final UUID customerId;
    private final List<Item> orderItems;
    private final String coupon;

    public MakeOrderForm(UUID customerId, List<Item> orderItems, String coupon) {
        this.customerId = customerId;
        this.orderItems = orderItems;
        this.coupon = coupon;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public List<Item> getOrderItems() {
        return orderItems;
    }

    public String getCoupon() {
        return coupon;
    }

    boolean hasValidItems() {
        return hasItems() && !hasInvalidItems();
    }

    private boolean hasItems() {
        return getOrderItems() != null && getOrderItems().size() > 0;
    }

    private boolean hasInvalidItems() {
        for (var item : getOrderItems()) {
            if (item.getPrice().compareTo(BigDecimal.ZERO) <= 0 ||
                item.getWeight() <= 0 ||
                item.getQuantity() < 1) {
                return true;
            }
        }
        return false;
    }
}
