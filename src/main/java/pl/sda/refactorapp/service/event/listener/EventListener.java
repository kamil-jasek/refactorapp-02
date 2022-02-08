package pl.sda.refactorapp.service.event.listener;

import pl.sda.refactorapp.service.event.Event;

public interface EventListener<T extends Event> {

    boolean supports(Event event);

    void handle(T event);
}
