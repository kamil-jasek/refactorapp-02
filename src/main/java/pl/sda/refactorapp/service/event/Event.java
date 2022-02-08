package pl.sda.refactorapp.service.event;

import java.time.Instant;
import java.util.UUID;

public abstract class Event {

    private final UUID id;
    private final Instant createTime;

    protected Event() {
        this.id = UUID.randomUUID();
        this.createTime = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public Instant getCreateTime() {
        return createTime;
    }
}
