package org.models.events;

import org.models.CalculationGeneration;
import org.models.Event;
import org.models.EventInput;
import org.models.State;

import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.HashMap;

public final class PaymentEvent extends Event {
    public PaymentEvent(Instant valueTime, Instant createTime) {
        super("payment", valueTime, createTime);
    }

    public PaymentEvent(int eventId, Instant valueTime, Instant createTime, List<CalculationGeneration> generations) {
        super(eventId, "payment", valueTime, createTime, generations);
    }

    @Override
    public State calculate(State previousState, EventInput input) {
        var year = valueTime().atZone(ZoneId.systemDefault()).getYear();

        var result = new State(new HashMap<>(previousState.paymentsPerYear()));
        var sum = result.paymentsPerYear().getOrDefault(year, 0);

        sum = sum + (int) input.inputs().get("amount");
        result.paymentsPerYear().put(year, sum);

        generations().add(new CalculationGeneration(input, result));
        return result;
    }
}
