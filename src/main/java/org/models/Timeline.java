package org.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
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

    public void addEvent(Event event, EventInput input) {
        int pos = 0;
        while (pos < events.size() && events.get(pos).valueTime().isBefore(event.valueTime()))
            pos++;
        events.add(pos, event);

        // calculate event
        State state = pos == 0
                ? new State(new HashMap<>())
                : events.get(pos - 1).generations().getLast().state();
        state = event.calculate(state, input);

        // re-calculate rest of event chain
        for (pos = pos + 1; pos < events.size(); pos++) {
            var nextEvent = events.get(pos);
            var calcInput = nextEvent.generations().getLast().input();
            state = nextEvent.calculate(state, calcInput);
        }
    }

    public State adjustEvent(int eventId, EventInput input) {
        int pos = 0;
        while (pos < events.size() && events.get(pos).eventId() != eventId)
            pos++;
        if (pos == events.size())
            throw new RuntimeException("eventid " + eventId + " not found");

        // calculate event
        var event = events.get(pos);
        State state = pos == 0
                ? new State(new HashMap<>())
                : events.get(pos - 1).generations().getLast().state();
        state = event.calculate(state, input);

        // re-calculate rest of event chain
        for (pos = pos + 1; pos < events.size(); pos++) {
            var nextEvent = events.get(pos);
            var calcInput = nextEvent.generations().getLast().input();
            state = nextEvent.calculate(state, calcInput);
        }
        return state;
    }
}
