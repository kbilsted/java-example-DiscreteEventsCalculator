package org.storage;

import org.models.Event;
import org.models.events.PaymentEvent;

import java.util.ArrayList;
import java.util.List;

public class CalculationGenerationsArchiver {
    private final DocumentStore store;

    CalculationGenerationsArchiver(DocumentStore store) {
        this.store = store;
    }

    public void Archive(int personId) {
        var person = store.getPerson(personId);
        if (person == null)
            return;

        var timeline = store.getTimeline(person, FetchParamenters.FullHistory);
        if (timeline == null)
            return;

        List<Event> history = timeline.getHistoricEvents();

        // Ensure history has same event order as timeline.
        for (int i = 0; i < timeline.getEvents().size(); i++) {
            var event = timeline.getEvents().get(i);

            if (i >= history.size()) {
                history.add(copyEventWithoutGenerations(event));
                continue;
            }

            if (history.get(i).eventId() != event.eventId()) {
                history.add(i, copyEventWithoutGenerations(event));
            }
        }

        // Move all but latest generation from timeline to matching history event by same index.
        for (int i = 0; i < timeline.getEvents().size(); i++) {
            var event = timeline.getEvents().get(i);
            var historyEvent = history.get(i);
            var generations = event.generations();
            if (generations.size() <= 1) {
                continue;
            }

            if (historyEvent.eventId() != event.eventId()) {
                throw new RuntimeException("history/timeline event order mismatch at index " + i);
            }

            var latest = generations.get(generations.size() - 1);

            for (int g = 0; g < generations.size() - 1; g++) {
                historyEvent.generations().add(generations.get(g));
            }

            generations.clear();
            generations.add(latest);
        }
    }

    private static Event copyEventWithoutGenerations(Event event) {
        if (event instanceof PaymentEvent) {
            return new PaymentEvent(event.eventId(), event.valueTime(), event.createTime(), new ArrayList<>());
        }

        throw new RuntimeException("unknown event type " + event.getClass().getName());
    }
}

