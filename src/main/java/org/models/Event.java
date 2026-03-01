package org.models;

import org.storage.GlobalId;

import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public record Event(int eventId, String name, Instant valueTime, Instant createTime,
                    List<CalculationGeneration> generations) {
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

        throw new RuntimeException("Unknown event " + name);
    }
}
