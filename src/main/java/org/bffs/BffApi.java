package org.bffs;

import org.models.*;
import org.models.Event;
import org.models.EventInput;
import org.models.events.PaymentEvent;
import org.storage.DocumentStore;
import org.storage.FetchParamenters;
import org.storage.GlobalId;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/** the api of the application */
public class BffApi {
    private final DocumentStore store;

    public BffApi(DocumentStore store) {
        this.store = store;
    }

    public Person createPerson(String name){
        var person = new Person(GlobalId.next(), name);
        store.addPerson(person);

        var timeline = new Timeline();
        store.addTimeline(person.id(), timeline);

        return person;
    }

    public Event createPaymentEvent(Person person, Instant valueTime, int amount) {
        Event event = new PaymentEvent(valueTime, Instant.now());
        EventInput input = new EventInput(GlobalId.next(), Instant.now(),  new HashMap<>(Map.of("amount", amount)));

        Timeline timeline = store.getTimeline(person.id(), FetchParamenters.Latest);
        timeline.addEvent(event, input);

        return event;
    }

    public State AdjustPaymentEvent(Person person, int eventId, int newAmount){
        Timeline timeline = store.getTimeline(person.id(), FetchParamenters.Latest);
        EventInput input = new EventInput(GlobalId.next(), Instant.now(),  new HashMap<>(Map.of("amount", newAmount)));

        var state =timeline.adjustEvent(eventId, input);

        return  state;
    }
}
