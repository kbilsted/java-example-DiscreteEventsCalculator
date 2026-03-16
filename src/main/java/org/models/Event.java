package org.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.*;
import lombok.experimental.Accessors;
import org.models.events.PaymentEvent;
import org.storage.GlobalId;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@Accessors(fluent = true)
@ToString
@EqualsAndHashCode
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = PaymentEvent.class, name = "payment")
})
public abstract class Event {
    private final int eventId;
    private final String name;
    private final Instant valueTime;
    private final Instant createTime;
    private final List<CalculationGeneration> generations;

    @JsonCreator
    protected Event(
            @JsonProperty("eventId") int eventId,
            @JsonProperty("name") String name,
            @JsonProperty("valueTime") Instant valueTime,
            @JsonProperty("createTime") Instant createTime,
            @JsonProperty("generations") List<CalculationGeneration> generations
    ) {
        this.eventId = eventId;
        this.name = name;
        this.valueTime = valueTime;
        this.createTime = createTime;
        this.generations = generations;
    }

    protected Event(@NonNull String name, @NonNull Instant valueTime, @NonNull Instant createTime) {
        this(GlobalId.next(), name, valueTime, createTime, new ArrayList<>());
    }

    /**
     * calculate the event with a deep cloned previous state S0, along with the event input
     * to return a new state S1
     */
    public abstract State calculate(State state, EventInput input);

    @JsonIgnore
    public State getState() {
        if(generations.isEmpty())
            throw new RuntimeException("called getState() before state calculation!");

        return generations.getLast().state();
    }
}
