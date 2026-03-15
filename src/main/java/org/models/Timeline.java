package org.models;

import lombok.*;
import org.storage.GlobalId;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

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

    public void addEvent(@NonNull Event event, @NonNull EventInput input) {
        int pos = 0;
        while (pos < events.size() && events.get(pos).valueTime().isBefore(event.valueTime()))
            pos++;
        events.add(pos, event);

        recalculateTimeline(pos, event, input);
    }

    public @NonNull State adjustEvent(int eventId, @NonNull EventInput input) {
        int pos = 0;
        while (pos < events.size() && events.get(pos).eventId() != eventId)
            pos++;
        if (pos == events.size())
            throw new RuntimeException("event id %d not found".formatted(eventId));

        return recalculateTimeline(pos, events.get(pos), input);
    }

    private State recalculateTimeline(int pos, Event event, EventInput input) {
        // calculate event and store it in calculation-generation
        State state = pos == 0
                ? new State(new HashMap<>())
                : events.get(pos - 1).getState().deepClone();

        state = event.calculate(state, input);
        event.generations().add(new CalculationGeneration(Instant.now(), input, state));

        // re-calculate rest of event chain and store them in calculation-generation
        for (pos = pos + 1; pos < events.size(); pos++) {
            var nextEvent = events.get(pos);
            var calcInput = nextEvent.generations().getLast().input();
            var clonedState = state.deepClone();

            state = nextEvent.calculate(clonedState, calcInput);
            nextEvent.generations().add(new CalculationGeneration(Instant.now(), input, state));
        }
        return state;
    }

    public @NonNull State getState() {
        if (events.isEmpty()) {
            return new State(new HashMap<>());
        }
        return events.getLast().getState();
    }

    private Stream<Event> getAllStates() {
        return Stream.concat(historicEvents.stream(), events.stream());
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
