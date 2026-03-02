package org.bffs;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.models.Event;
import org.models.Person;
import org.storage.DocumentStore;
import org.storage.FetchParamenters;
import org.storage.GlobalId;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class DocumentStoreTest {
    DocumentStore store;
    BffApi bffApi;
    Person person;

    @BeforeEach
    void setUp() {
        GlobalId.reset(1);
        store = new DocumentStore();
        bffApi = new BffApi(store);

        person = bffApi.createPerson("anders and");
    }

    @Test
    void when_creating_a_timeline_for_unknown_person_Then_create_person_and_timeline() {
        bffApi.createPaymentEvent(person, Instant.parse("2026-01-01T00:00:00Z"), 100);

        assertEquals(1, store.countPeople());

        assertEquals(1, store.countTimelines());

        List<Event> events = store.getTimeline(person.id(), FetchParamenters.Latest).getEvents();
        assertEquals("payment", events.getLast().name());
        assertEquals(100, events.getLast().generations().getLast().input().inputs().get("amount"));
    }

    @Test
    void When_creating_two_events_Then_events_are_ordered_by_valuedate_and_are_calculated() {
        var event1 = bffApi.createPaymentEvent(person, Instant.parse("2026-01-01T00:00:00Z"), 100);
        var event2 = bffApi.createPaymentEvent(person, Instant.parse("2026-02-01T00:00:00Z"), 110);

        List<Event> events = store.getTimeline(person.id(), FetchParamenters.Latest).getEvents();
        assertEquals(2, events.size());

        assertEquals(event2.eventId(), events.getLast().eventId(), "ensure ordering of value time");

        HashMap<Integer, Integer> paymentsPerYear = events.getLast().generations().getLast().state().paymentsPerYear();
        assertNull(paymentsPerYear.get(2025), "no payments for 2025");
        assertEquals(210, paymentsPerYear.get(2026), "accumulate amount");
    }

    @Test
    void When_adjusting_an_event_Then_all_events_from_the_event_is_recalculated() {
        var event1 = bffApi.createPaymentEvent(person, Instant.parse("2026-01-01T00:00:00Z"), 100);
        var event2 = bffApi.createPaymentEvent(person, Instant.parse("2026-02-01T00:00:00Z"), 110);

        assertEquals(2, store.getTimeline(person.id(), FetchParamenters.FullHistory).countSumCalculationGenerations());

        var state = bffApi.adjustPaymentEvent(person, event1.eventId(), 90);

        assertEquals(200, state.paymentsPerYear().get(2026));
        assertEquals(4, store.getTimeline(person.id(), FetchParamenters.FullHistory).countSumCalculationGenerations());
    }
}
