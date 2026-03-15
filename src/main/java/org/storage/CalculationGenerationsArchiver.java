package org.storage;

import org.models.Event;
import org.models.events.PaymentEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * async archiver. assume the timeline is locked while operating.
 * The archiver moves outdate data to another document in the document store making it
 * faster to fetch the data only relevant to most use cases.
 * <br>
 * we do this asynchronously, rather than during the save operation, to ensure we only fetch
 * historic data when really needed.
 */
public class CalculationGenerationsArchiver {
    private final DocumentStore store;

    public CalculationGenerationsArchiver(DocumentStore store) {
        this.store = store;
    }

    public void archive(int personId) {
        store.getPerson(personId)
                .flatMap(x -> store.getTimeline(x, FetchParamenters.FullHistory))
                .flatMap(timeline -> {
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
                        var latest = generations.getLast();

                        for (int g = 0; g < generations.size() - 1; g++) {
                            historyEvent.generations().add(generations.get(g));
                        }
                        generations.clear();
                        generations.add(latest);
                    }

                    return java.util.Optional.empty();
                });
    }

    private static Event copyEventWithoutGenerations(Event event) {
        if (event instanceof PaymentEvent) {
            return new PaymentEvent(event.eventId(), event.valueTime(), event.createTime(), new ArrayList<>());
        }

        throw new RuntimeException("unknown event type " + event.getClass().getName());
    }
}
