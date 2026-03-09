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

        recalculateTimeline(pos, event, input);
    }

    public @NonNull State adjustEvent(int eventId,@NonNull EventInput input) {
        int pos = 0;
        while (pos < events.size() && events.get(pos).eventId() != eventId)
            pos++;
        if (pos == events.size())
            throw new RuntimeException("event id %d not found".formatted(eventId));

        return recalculateTimeline(pos, events.get(pos), input);
    }

    private State recalculateTimeline(int pos, Event event, EventInput input)
    {
        State state = pos == 0
                ? new State(new HashMap<>())
                : getState(pos - 1).deepClone();

        state = event.calculate(state, input);
        event.generations().add(new CalculationGeneration(input, state));

        // re-calculate rest of event chain
        for (pos = pos + 1; pos < events.size(); pos++) {
            var nextEvent = events.get(pos);
            var calcInput = nextEvent.generations().getLast().input();
            var clonedState = state.deepClone();

            state = nextEvent.calculate(clonedState, calcInput);
            nextEvent.generations().add(new CalculationGeneration(input, state));
        }
        return state;
    }

    public @NonNull State getState() {
        if (events.isEmpty()) {
            return new State(new HashMap<>());
        }
        return getState(events.size() - 1);
    }

    private @NonNull State getState(int eventIndex) {
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
