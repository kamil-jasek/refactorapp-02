package pl.sda.refactorapp.service.discountcalculator;

import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableMap;
import static java.util.Objects.requireNonNull;
import static pl.sda.refactorapp.util.ArgumentValidator.check;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import pl.sda.refactorapp.entity.Item;

public final class DiscountContext {

    private final UUID customerId;
    private final List<Item> items;
    private final Map<String, Object> parameters;
    private final float maxDiscount;
    private float currentDiscount;

    public DiscountContext(UUID customerId,
        List<Item> items,
        Map<String, Object> parameters,
        float maxDiscount) {
        requireNonNull(customerId);
        requireNonNull(items);
        check(!items.isEmpty(), "items is empty");
        requireNonNull(parameters);
        check(maxDiscount >= 0 && maxDiscount <= 1, "invalid max discount");
        this.customerId = customerId;
        this.items = unmodifiableList(items);
        this.parameters = unmodifiableMap(parameters);
        this.maxDiscount = maxDiscount;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public List<Item> getItems() {
        return items;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public float getMaxDiscount() {
        return maxDiscount;
    }

    public float getCurrentDiscount() {
        return currentDiscount;
    }

    public void updateCurrentDiscount(float value) {
        check(value >= 0 && value <= 1, "invalid discount");
        this.currentDiscount = value;
    }
}
