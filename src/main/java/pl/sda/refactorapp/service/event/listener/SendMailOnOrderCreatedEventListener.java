package pl.sda.refactorapp.service.event.listener;

import pl.sda.refactorapp.service.MailService;
import pl.sda.refactorapp.service.event.Event;
import pl.sda.refactorapp.service.event.OrderCreatedEvent;

final class SendMailOnOrderCreatedEventListener implements EventListener<OrderCreatedEvent> {

    @Override
    public boolean supports(Event event) {
        return event instanceof OrderCreatedEvent;
    }

    @Override
    public void handle(OrderCreatedEvent event) {
        MailService.sendMail(event.getCustomerEmail(), "order created", "..............");
    }
}
