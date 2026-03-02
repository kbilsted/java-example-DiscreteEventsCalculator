package org.models;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.storage.GlobalId;

import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Getter
@Accessors(fluent = true)
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class Event {
    private final int eventId;
    private final String name;
    private final Instant valueTime;
    private final Instant createTime;
    private final List<CalculationGeneration> generations;

    public Event(String name, Instant valueTime, Instant createTime) {
        this(GlobalId.next(), name, valueTime, createTime, new ArrayList<>());
    }

    public State calculate(State previousState, EventInput input) {
        if (name.equals("payment")) {
            var year = valueTime.atZone(ZoneId.systemDefault()).getYear();

            var result = new State(new HashMap<>(previousState.paymentsPerYear()));
            var sum = result.paymentsPerYear().getOrDefault(year, 0);

            sum = sum + (int) input.inputs().get("amount");
            result.paymentsPerYear().put(year, sum);

            generations.add(new CalculationGeneration(input, result));
            return result;
        }

        if(name.equals("disbursement"))
        {
            State state = new State(new HashMap<>(previousState.paymentsPerYear()));
            generations.add(new CalculationGeneration(input, state));
            return  state;
        }

        throw new RuntimeException("Unknown event " + name);
    }
}

