package org.models;

import lombok.*;
import lombok.experimental.Accessors;
import org.models.events.PaymentEvent;
import org.storage.GlobalId;

import java.time.Instant;
import java.util.ArrayList;

@Accessors(fluent = true)
public record Event(int eventId, EventType type, Instant valueTime, Instant createTime,
                    ArrayList<CalculationGeneration> generations) {

    public Event(@NonNull EventType type, @NonNull Instant valueTime, @NonNull Instant createTime) {
        this(GlobalId.next(), type, valueTime, createTime, new ArrayList<>());
    }

    /**
     * calculate the event with a state S_0, along with the event input
     * to return a new state S_1
     */
    public State calculate(State state, EventInput input) {
        switch (type) {
            case PAYMENT -> {
                return new PaymentEvent(this).calculate(state, input);
            }
            default -> throw new RuntimeException("unknown event type " + type);
        }
    }

    public State getState() {
        if (generations.isEmpty())
            throw new RuntimeException("called getState() before state calculation!");

        return generations.getLast().state();
    }

    public Event deepClone() {
        var clonedGenerations = new ArrayList<CalculationGeneration>(generations.size());
        for (var generation : generations) {
            clonedGenerations.add(generation.deepClone());
        }
        return new Event(eventId, type, valueTime, createTime, clonedGenerations);
    }
}
