package org.bffs;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.models.Event;
import org.models.Person;
import org.models.Timeline;
import org.storage.CalculationGenerationsArchiver;
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

        List<Event> events = store.getTimeline(person, FetchParamenters.Latest).getEvents();
        assertEquals("payment", events.getLast().name());
        assertEquals(100, events.getLast().generations().getLast().input().inputs().get("amount"));
    }

    @Test
    void When_creating_two_events_Then_events_are_ordered_by_valuedate_and_are_calculated() {
        var event1 = bffApi.createPaymentEvent(person, Instant.parse("2026-01-01T00:00:00Z"), 100);
        var event2 = bffApi.createPaymentEvent(person, Instant.parse("2026-02-01T00:00:00Z"), 110);

        var timeline  = store.getTimeline(person, FetchParamenters.Latest);
        List<Event> events = timeline.getEvents() ;
        assertEquals(2, events.size());

        assertEquals(event2.eventId(), events.getLast().eventId(), "ensure ordering of value time");

        HashMap<Integer, Integer> paymentsPerYear = store.getTimeline(person, FetchParamenters.Latest).getState().paymentsPerYear();
        assertNull(paymentsPerYear.get(2025), "no payments for 2025");
        assertEquals(210, paymentsPerYear.get(2026), "accumulate amount");
    }

    @Test
    void When_adjusting_an_event_Then_all_events_from_the_event_is_recalculated() {
        var event1 = bffApi.createPaymentEvent(person, Instant.parse("2026-01-01T00:00:00Z"), 100);
        var event2 = bffApi.createPaymentEvent(person, Instant.parse("2026-02-01T00:00:00Z"), 110);

        assertEquals(2, store.getTimeline(person, FetchParamenters.FullHistory).countSumCalculationGenerations());

        var state = bffApi.adjustPaymentEvent(person, event1.eventId(), 90);

        assertEquals(200, state.paymentsPerYear().get(2026));
        assertEquals(4, store.getTimeline(person, FetchParamenters.FullHistory).countSumCalculationGenerations());
    }

    @Test
    void when_archiving_Then_all_historic_calculations_is_moved_to_history() {
        var event1 = bffApi.createPaymentEvent(person, Instant.parse("2026-01-01T00:00:00Z"), 100);
        var event2 = bffApi.createPaymentEvent(person, Instant.parse("2026-02-01T00:00:00Z"), 110);
        for (int i = 1; i < 100; i++)
            bffApi.adjustPaymentEvent(person, event1.eventId(), i);

        // precondition no historic data
        Timeline timeline = store.getTimeline(person, FetchParamenters.FullHistory);
        int total2026 = timeline.getState().paymentsPerYear().get(2026);
        assertEquals(200, timeline.countSumCalculationGenerations());
        assertEquals(0, timeline.getHistoricEvents().size());

        // act
        var archiver = new CalculationGenerationsArchiver(store);
        archiver.Archive(person.id());

        // assert archiving has data
        timeline = store.getTimeline(person, FetchParamenters.FullHistory);
        var history = timeline.getHistoricEvents();
        assertEquals(2, history.size());
        assertEquals(198, timeline.countSumHistoricCalculationGenerations());

        // assert historic data has been moved
        timeline = store.getTimeline(person, FetchParamenters.Latest);
        assertEquals(2, timeline.countSumCalculationGenerations());
        assertEquals(0, timeline.countSumHistoricCalculationGenerations());

        // assert same calculation result
        int newTotal2026 = timeline.getState().paymentsPerYear().get(2026);
        assertEquals(total2026, newTotal2026);
    }
}
