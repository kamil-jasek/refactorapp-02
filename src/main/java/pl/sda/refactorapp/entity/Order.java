package pl.sda.refactorapp.entity;

import static java.util.UUID.randomUUID;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import pl.sda.refactorapp.annotation.Entity;
import pl.sda.refactorapp.annotation.Id;
import pl.sda.refactorapp.annotation.OneToMany;
import pl.sda.refactorapp.service.MakeOrderForm;
import pl.sda.refactorapp.service.event.OrderCreatedEvent;
import pl.sda.refactorapp.service.event.OrderCreatedEvent.OrderItem;

/**
 * The customer order
 */
@Entity
public class Order {

    // order statuses
    public static final int ORDER_STATUS_WAITING = 1;
    public static final int ORDER_STATUS_SENT = 2;
    public static final int ORDER_STATUS_DELIVERED = 3;

    @Id
    private UUID id;

    // customer id
    private UUID cid;

    private LocalDateTime ctime;

    // value between 0 and 1
    private float discount;

    @OneToMany
    private List<Item> items;

    private int status;

    public BigDecimal deliveryCost;

    // only for framework
    public Order() {
    }

    public Order(UUID customerId, List<Item> orderItems) {
        this.id = randomUUID();
        this.cid = customerId;
        this.items = orderItems;
        this.ctime = LocalDateTime.now();
        this.status = ORDER_STATUS_WAITING;
    }

    public static Order createFrom(MakeOrderForm form) {
        return new Order(form.getCustomerId(), form.getOrderItems());
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getCid() {
        return cid;
    }

    public void setCid(UUID cid) {
        this.cid = cid;
    }

    public LocalDateTime getCtime() {
        return ctime;
    }

    public void setCtime(LocalDateTime ctime) {
        this.ctime = ctime;
    }

    public float getDiscount() {
        return discount;
    }

    public void setDiscount(float discount) {
        this.discount = discount;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }


    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public BigDecimal getDeliveryCost() {
        return deliveryCost;
    }

    public void computeDelivery() {
        var totalPrice = BigDecimal.ZERO;
        var totalWeight = 0;
        for (Item i : getItems()) {
            totalPrice = totalPrice.add(i.getPrice().multiply(new BigDecimal(i.getQuantity()))); // totalPrice = totalPrice + (i.price * i.quantity)
            totalWeight += (i.getQuantity() * i.getWeight());
        }
        if (totalPrice.compareTo(new BigDecimal(250)) > 0 && totalWeight < 1) {
            this.deliveryCost = BigDecimal.ZERO;
        } else if (totalWeight < 1) {
            this.deliveryCost = new BigDecimal(15);
        } else if (totalWeight < 5) {
            this.deliveryCost = new BigDecimal(35);
        } else {
            this.deliveryCost = new BigDecimal(50);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Order order = (Order) o;
        return Float.compare(order.discount, discount) == 0 && status == order.status && Objects.equals(
            id, order.id) && Objects.equals(cid, order.cid) && Objects.equals(ctime, order.ctime)
            && Objects.equals(items, order.items) && Objects.equals(deliveryCost, order.deliveryCost);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, cid, ctime, discount, items, status, deliveryCost);
    }

    @Override
    public String toString() {
        return "Order{" +
            "id=" + id +
            ", cid=" + cid +
            ", ctime=" + ctime +
            ", discount=" + discount +
            ", items=" + items +
            ", status=" + status +
            ", deliveryCost=" + deliveryCost +
            '}';
    }

    public OrderCreatedEvent toOrderCreatedEvent(String email) {
        return new OrderCreatedEvent(getId(),
            getCid(),
            email,
            getItems()
                .stream()
                .map(item -> new OrderItem(item.getId(),
                    item.getName(),
                    item.getPrice(),
                    item.getQuantity(),
                    item.getWeight())).collect(Collectors.toList()),
            getCtime(),
            getDiscount(),
            getDeliveryCost());
    }
}
