package pl.sda.refactorapp.controller;

import java.util.List;
import java.util.UUID;
import pl.sda.refactorapp.annotation.Controller;
import pl.sda.refactorapp.annotation.Inject;
import pl.sda.refactorapp.entity.Item;
import pl.sda.refactorapp.service.MakeOrderForm;
import pl.sda.refactorapp.service.OrderService;

@Controller
public class OrderController {

    @Inject
    private OrderService orderService;

    public String postMakeOrder(UUID customer, List<Item> items, String coupon) {
        if (orderService.makeOrder(new MakeOrderForm(customer, items, coupon))) {
            return "make-order-success-page";
        } else {
            return "make-order-error-page";
        }
    }

    public String postMakeOrder(UUID customer, List<Item> items, float discount) {
        if (orderService.makeOrder(customer, items, discount)) {
            return "make-order-success-page";
        } else {
            return "make-order-error-page";
        }
    }

    public String updateStatus(UUID oid, int newStatus) {
        if (orderService.updateOrderStatus(oid, newStatus)) {
            return "change-order-status-success-page";
        } else {
            return "change-order-error-page";
        }
    }
}
