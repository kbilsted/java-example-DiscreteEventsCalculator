package org.models.events;

import org.models.Event;
import org.models.EventInput;
import org.models.State;

import java.time.ZoneId;

public final class PaymentEvent  {
    Event event;

    public PaymentEvent(Event event) {
        this.event = event;
    }

    public State calculate(State state, EventInput input) {
        var year = this.event.valueTime().atZone(ZoneId.systemDefault()).getYear();

        var sum = state.paymentsPerYear().getOrDefault(year, 0);
        sum = sum + (int) input.inputs().get("amount");
        state.paymentsPerYear().put(year, sum);

        return state;
    }
}
