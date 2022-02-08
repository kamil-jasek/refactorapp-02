package pl.sda.refactorapp.service.event;

import static java.util.Objects.requireNonNull;
import static pl.sda.refactorapp.util.ArgumentValidator.check;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import pl.sda.refactorapp.annotation.Service;
import pl.sda.refactorapp.annotation.Transactional;
import pl.sda.refactorapp.service.event.listener.EventListener;

@Service
public final class EventPublisher {

    private final EventRepository eventRepository;
    private final List<EventListener<Event>> eventListeners;
    private final ScheduledExecutorService executor;

    public EventPublisher(EventRepository eventRepository,
        List<EventListener<Event>> eventListeners) {
        requireNonNull(eventRepository);
        requireNonNull(eventListeners);
        check(!eventListeners.isEmpty(), "listeners is empty");
        this.eventRepository = eventRepository;
        this.eventListeners = eventListeners;
        this.executor = Executors.newSingleThreadScheduledExecutor();
        this.executor.scheduleAtFixedRate(this::publishInternal, 0, 1, TimeUnit.SECONDS);
    }

    @Transactional
    public void publish(Event event) {
        eventRepository.save(event);
    }

    private void publishInternal() {
        final var events = eventRepository.findAllNotHandled();
        events.forEach(event -> eventListeners
            .stream()
            .filter(listener -> listener.supports(event))
            .forEach(listener -> listener.handle(event)));
        eventRepository.deleteAll(events);
    }
}
