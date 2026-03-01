package org.bffs;

import org.models.*;
import org.storage.DocumentStore;
import org.storage.GlobalId;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class BffApi {
    private final DocumentStore store;

    public BffApi(DocumentStore store) {
        this.store = store;
    }

    public Person createPerson(String name){
        var person = new Person(GlobalId.next(), name);
        store.people().add(person);

        var timeline = new Timeline();
        store.timelines().put(person.id(), timeline);

        return person;
    }

    public Event createPaymentEvent(Person person, Instant valueTime, int amount) {
        Event event = new Event("payment", valueTime, Instant.now());
        EventInput input = new EventInput(GlobalId.next(), Instant.now(),  new HashMap<>(Map.of("amount", amount)));

        Timeline timeline = store.timelines().get(person.id());
        timeline.add(event, input);

        return event;
    }
}
