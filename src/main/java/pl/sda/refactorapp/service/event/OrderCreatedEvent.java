package pl.sda.refactorapp.service.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public final class OrderCreatedEvent extends Event {

    private final UUID orderId;
    private final UUID customerId;
    private final String customerEmail;
    private final List<OrderItem> orderItems;
    private final LocalDateTime orderCreateTime;
    private final float discount;
    private final BigDecimal deliveryCost;

    public static final class OrderItem {
        private final UUID id;
        private final String name;
        private final BigDecimal price;
        private final int quantity;
        private final Float weight;

        public OrderItem(UUID id, String name, BigDecimal price, int quantity, Float weight) {
            this.id = id;
            this.name = name;
            this.price = price;
            this.quantity = quantity;
            this.weight = weight;
        }

        public UUID getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public BigDecimal getPrice() {
            return price;
        }

        public int getQuantity() {
            return quantity;
        }

        public Float getWeight() {
            return weight;
        }
    }

    public OrderCreatedEvent(UUID orderId,
        UUID customerId,
        String customerEmail,
        List<OrderItem> orderItems,
        LocalDateTime orderCreateTime,
        float discount,
        BigDecimal deliveryCost) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.customerEmail = customerEmail;
        this.orderItems = orderItems;
        this.orderCreateTime = orderCreateTime;
        this.discount = discount;
        this.deliveryCost = deliveryCost;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public LocalDateTime getOrderCreateTime() {
        return orderCreateTime;
    }

    public float getDiscount() {
        return discount;
    }

    public BigDecimal getDeliveryCost() {
        return deliveryCost;
    }
}
