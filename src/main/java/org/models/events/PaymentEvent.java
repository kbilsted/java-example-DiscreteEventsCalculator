package org.models.events;

import org.models.CalculationGeneration;
import org.models.Event;
import org.models.EventInput;
import org.models.State;

import java.time.Instant;
import java.time.ZoneId;
import java.util.List;

public final class PaymentEvent extends Event {
    public PaymentEvent(Instant valueTime, Instant createTime) {
        super("payment", valueTime, createTime);
    }

    public PaymentEvent(int eventId, Instant valueTime, Instant createTime, List<CalculationGeneration> generations) {
        super(eventId, "payment", valueTime, createTime, generations);
    }

    @Override
    public State calculate(State state, EventInput input) {
        var year = valueTime().atZone(ZoneId.systemDefault()).getYear();

        var sum = state.paymentsPerYear().getOrDefault(year, 0);
        sum = sum + (int) input.inputs().get("amount");
        state.paymentsPerYear().put(year, sum);

        return state;
    }
}
