package org.models;

import lombok.*;
import lombok.experimental.Accessors;
import org.storage.GlobalId;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@Accessors(fluent = true)
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public abstract class Event {
    private final int eventId;
    private final String name;
    private final Instant valueTime;
    private final Instant createTime;
    private final List<CalculationGeneration> generations;

    protected Event(@NonNull String name, @NonNull Instant valueTime, @NonNull Instant createTime) {
        this(GlobalId.next(), name, valueTime, createTime, new ArrayList<>());
    }

    public abstract State calculate(State previousState, EventInput input);
}
