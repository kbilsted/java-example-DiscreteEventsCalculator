package org.example.eventstream;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;

public final class Event {
    private final long eventId;
    private final String eventName;
    private final String personId;
    private final LocalDate valorDate;
    private final Instant createdAt;

    public Event(long eventId, String eventName, String personId, LocalDate valorDate, Instant createdAt) {
        this.eventId = eventId;
        this.eventName = Objects.requireNonNull(eventName, "eventName must not be null");
        this.personId = Objects.requireNonNull(personId, "personId must not be null");
        this.valorDate = Objects.requireNonNull(valorDate, "valorDate must not be null");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt must not be null");
    }

    public long getEventId() {
        return eventId;
    }

    public String getEventName() {
        return eventName;
    }

    public String getPersonId() {
        return personId;
    }

    public LocalDate getValorDate() {
        return valorDate;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
