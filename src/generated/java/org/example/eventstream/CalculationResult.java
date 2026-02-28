package org.example.eventstream;

import java.time.Instant;

public final class CalculationResult {
    private final long eventId;
    private final long inputId;
    private final int generation;
    private final long calculatedRealTime;
    private final Instant calculatedAt;
    private final int resultAmount;

    public CalculationResult(
            long eventId,
            long inputId,
            int generation,
            long calculatedRealTime,
            Instant calculatedAt,
            int resultAmount
    ) {
        this.eventId = eventId;
        this.inputId = inputId;
        this.generation = generation;
        this.calculatedRealTime = calculatedRealTime;
        this.calculatedAt = calculatedAt;
        this.resultAmount = resultAmount;
    }

    public long getEventId() {
        return eventId;
    }

    public long getInputId() {
        return inputId;
    }

    public int getGeneration() {
        return generation;
    }

    public long getCalculatedRealTime() {
        return calculatedRealTime;
    }

    public Instant getCalculatedAt() {
        return calculatedAt;
    }

    public int getResultAmount() {
        return resultAmount;
    }
}
