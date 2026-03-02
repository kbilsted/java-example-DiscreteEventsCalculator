package org.models;

import lombok.*;
import org.storage.GlobalId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class Timeline {
    int id;
    List<Event> events;
    List<Event> historicEvents;

    public Timeline() {
        this(GlobalId.next(), new ArrayList<>(), new ArrayList<>());
    }

    public void addEvent(@NonNull Event event,@NonNull EventInput input) {
        int pos = 0;
        while (pos < events.size() && events.get(pos).valueTime().isBefore(event.valueTime()))
            pos++;
        events.add(pos, event);

        // calculate event
        State state = pos == 0
                ? new State(new HashMap<>())
                : getState(pos - 1);
        state = event.calculate(state, input);

        // re-calculate rest of event chain
        for (pos = pos + 1; pos < events.size(); pos++) {
            var nextEvent = events.get(pos);
            var calcInput = nextEvent.generations().getLast().input();
            state = nextEvent.calculate(state, calcInput);
        }
    }

    public State adjustEvent(int eventId,@NonNull EventInput input) {
        int pos = 0;
        while (pos < events.size() && events.get(pos).eventId() != eventId)
            pos++;
        if (pos == events.size())
            throw new RuntimeException("eventid " + eventId + " not found");

        // calculate event
        var event = events.get(pos);
        State state = pos == 0
                ? new State(new HashMap<>())
                : getState(pos - 1);
        state = event.calculate(state, input);

        // re-calculate rest of event chain
        for (pos = pos + 1; pos < events.size(); pos++) {
            var nextEvent = events.get(pos);
            var calcInput = nextEvent.generations().getLast().input();
            state = nextEvent.calculate(state, calcInput);
        }
        return state;
    }

    public State getState() {
        if (events.isEmpty()) {
            return new State(new HashMap<>());
        }
        return getState(events.size() - 1);
    }

    private State getState(int eventIndex) {
        return events.get(eventIndex).generations().getLast().state();
    }

    public int countSumCalculationGenerations() {
        return events.stream()
                .mapToInt(x -> x.generations().size())
                .sum();
    }

    public int countSumHistoricCalculationGenerations() {
        return historicEvents.stream()
                .mapToInt(x -> x.generations().size())
                .sum();
    }
}
