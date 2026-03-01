package org.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public record Timeline(List<Event> events) {
    public Timeline() {
        this(new ArrayList<>());
    }

    public void add(Event event, EventInput input) {
        int pos = 0;
        while (pos < events.size() && events.get(pos).valueTime().isAfter(event.valueTime())) {
            pos++;
        }
        events.add(pos, event);

        State state = pos == 0
                ? new State(new HashMap<>())
                : events.get(pos - 1).generations().getLast().state();

        state = event.calculate(state, input);

        for (pos = pos + 1; pos < events.size(); pos++) {
            var nextEvent = events.get(pos);
            var calcInput = nextEvent.generations().getLast().input();
            state = nextEvent.calculate(state, calcInput);
        }
    }
}
