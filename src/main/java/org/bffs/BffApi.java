package org.bffs;

import org.models.*;
import org.models.events.PaymentEvent;
import org.storage.DocumentStore;
import org.storage.FetchParamenters;
import org.storage.GlobalId;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * the api of the application
 */
public class BffApi {
    private final DocumentStore documentStore;

    public BffApi(DocumentStore documentStore) {
        this.documentStore = documentStore;
    }

    public Person createPerson(String name) {
        var person = new Person(GlobalId.next(), name);
        documentStore.storePerson(person);

        var timeline = new Timeline();
        documentStore.storeTimeline(person, timeline);

        return person;
    }

    public Event createPaymentEvent(Person person, Instant valueTime, int amount) {
        Event event = new PaymentEvent(valueTime, Instant.now());
        EventInput input = new EventInput(GlobalId.next(), Instant.now(), new HashMap<>(Map.of("amount", amount)));

        return documentStore
                .getTimeline(person, FetchParamenters.Latest)
                .map(timeline -> {
                    timeline.addEvent(event, input);
                    if (!documentStore.storeTimeline(person, timeline))
                        throw new RuntimeException("Timeline was modified while adding event. Cannot add event");
                    return event;
                })
                .orElseThrow(() -> new RuntimeException("Timeline not found for person " + person.id()));
    }

    public State adjustPaymentEvent(Person person, int eventId, int newAmount) {
        EventInput input = new EventInput(GlobalId.next(), Instant.now(), new HashMap<>(Map.of("amount", newAmount)));

        return documentStore
                .getTimeline(person, FetchParamenters.Latest)
                .map(timeline -> {
                    var state = timeline.adjustEvent(eventId, input);
                    if (!documentStore.storeTimeline(person, timeline))
                        throw new RuntimeException("Timeline was modified while adjusting event. Cannot adjust event");
                    return state;
                })
                .orElseThrow(() -> new RuntimeException("Timeline not found for person " + person.id()));
    }
}
