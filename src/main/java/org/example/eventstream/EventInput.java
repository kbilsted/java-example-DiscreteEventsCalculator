package org.example.eventstream;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;

public final class EventInput {
    private final long inputId;
    private final long eventId;
    private final LocalDate valorDate;
    private final int amount;
    private final long realTime;
    private final Instant createdAt;

    public EventInput(long inputId, long eventId, LocalDate valorDate, int amount, long realTime, Instant createdAt) {
        this.inputId = inputId;
        this.eventId = eventId;
        this.valorDate = Objects.requireNonNull(valorDate, "valorDate must not be null");
        this.amount = amount;
        this.realTime = realTime;
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt must not be null");
    }

    public long getInputId() {
        return inputId;
    }

    public long getEventId() {
        return eventId;
    }

    public LocalDate getValorDate() {
        return valorDate;
    }

    public int getAmount() {
        return amount;
    }

    public long getRealTime() {
        return realTime;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
