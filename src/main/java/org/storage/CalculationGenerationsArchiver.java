package org.storage;

import org.models.Event;
import org.models.events.PaymentEvent;

import java.util.ArrayList;
import java.util.List;

/** async archiver. assume the timeline is locked while operating */
public class CalculationGenerationsArchiver {
    private final DocumentStore store;

    public CalculationGenerationsArchiver(DocumentStore store) {
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

        for (int i = 0; i < timeline.getEvents().size(); i++) {
            var event = timeline.getEvents().get(i);

            // ensure event in history
            if (i >= history.size()) {
                history.add(copyEventWithoutGenerations(event));
            } else if (history.get(i).eventId() != event.eventId()) {
                history.add(i, copyEventWithoutGenerations(event));
            }

            // move all but latest generations
            var historyEvent = history.get(i);
            var generations = event.generations();
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
