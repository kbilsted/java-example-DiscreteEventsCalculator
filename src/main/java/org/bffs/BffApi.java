package org.bffs;

import org.models.*;
import org.storage.PersonRepository;
import org.storage.FetchParamenters;
import org.storage.GlobalId;
import org.storage.TimelineRepository;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Backend For Frontend - the api of the application
 */
public class BffApi {
    private final PersonRepository personRepository;
    private final TimelineRepository timelineRepository;

    public BffApi(PersonRepository personRepository, TimelineRepository timelineRepository) {
        this.personRepository = personRepository;
        this.timelineRepository = timelineRepository;
    }

    public Person createPerson(String name) {
        var person = new Person(GlobalId.next(), name);
        personRepository.storePerson(person);

        var timeline = new Timeline();
        timelineRepository.storeTimeline(person, timeline);

        return person;
    }

    public Event createPaymentEvent(Person person, Instant valueTime, int amount) {
        Event event = new Event(EventType.PAYMENT, valueTime, Instant.now());
        EventInput input = new EventInput(GlobalId.next(), Instant.now(), new HashMap<>(Map.of("amount", amount)));

        return timelineRepository
                .getTimeline(person, FetchParamenters.Latest)
                .map(timeline -> {
                    timeline.addEvent(event, input);
                    if (!timelineRepository.storeTimeline(person, timeline))
                        throw new RuntimeException("Timeline was modified while adding event. Cannot add event");
                    return event;
                })
                .orElseThrow(() -> new RuntimeException("Timeline not found for person " + person.id()));
    }

    public State adjustPaymentEvent(Person person, int eventId, int newAmount) {
        EventInput input = new EventInput(GlobalId.next(), Instant.now(), new HashMap<>(Map.of("amount", newAmount)));

        return timelineRepository
                .getTimeline(person, FetchParamenters.Latest)
                .map(timeline -> {
                    var state = timeline.adjustEvent(eventId, input);
                    if (!timelineRepository.storeTimeline(person, timeline))
                        throw new RuntimeException("Timeline was modified while adjusting event. Cannot adjust event");
                    return state;
                })
                .orElseThrow(() -> new RuntimeException("Timeline not found for person " + person.id()));
    }
}
