package org.bffs;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.models.Event;
import org.models.EventType;
import org.models.Person;
import org.models.Timeline;
import org.storage.CalculationGenerationsArchiver;
import org.storage.DocumentStore;
import org.storage.FetchParamenters;
import org.storage.GlobalId;
import org.storage.PersonRepository;
import org.storage.TimelineRepository;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DocumentStoreTest {
    DocumentStore store;
    PersonRepository personRepository;
    TimelineRepository timelineRepository;
    BffApi bffApi;
    Person person;

    @BeforeEach
    void setUp() {
        GlobalId.reset(1);
        store = new DocumentStore();
        personRepository = new PersonRepository(store);
        timelineRepository = new TimelineRepository(store);
        bffApi = new BffApi(personRepository, timelineRepository);

        person = bffApi.createPerson("anders and");
    }

    @Test
    void When_querying_a_nonexisting_person_Then_fail() {
        assertEquals("Timeline not found for person 3",
                assertThrows(
                        RuntimeException.class,
                        () -> bffApi.createPaymentEvent(new Person("8"), Instant.MIN, 100))
                        .getMessage());
    }

    @Test
    void when_creating_a_timeline_for_unknown_person_Then_create_person_and_timeline() {
        bffApi.createPaymentEvent(person, Instant.parse("2026-01-01T00:00:00Z"), 100);

        assertEquals(1, personRepository.countPeople());

        assertEquals(1, timelineRepository.countTimelines());

        List<Event> events = timelineRepository.getTimeline(person, FetchParamenters.Latest).get().getEvents();
        assertEquals(EventType.PAYMENT, events.getLast().type());
        assertEquals(100, events.getLast().generations().getLast().input().inputs().get("amount"));
    }

    @Test
    void When_creating_two_events_Then_events_are_ordered_by_valuedate_and_are_calculated() {
        var event1 = bffApi.createPaymentEvent(person, Instant.parse("2026-01-01T00:00:00Z"), 100);
        var event2 = bffApi.createPaymentEvent(person, Instant.parse("2026-02-01T00:00:00Z"), 110);

        var timeline = timelineRepository.getTimeline(person, FetchParamenters.Latest);
        List<Event> events = timeline.get().getEvents();
        assertEquals(2, events.size());

        assertEquals(event2.eventId(), events.getLast().eventId(), "ensure ordering of value time");

        HashMap<Integer, Integer> paymentsPerYear = timelineRepository.getTimeline(person, FetchParamenters.Latest).get().getState().paymentsPerYear();
        assertNull(paymentsPerYear.get(2025), "no payments for 2025");
        assertEquals(210, paymentsPerYear.get(2026), "accumulate amount");
    }

    @Test
    void When_adjusting_an_event_Then_all_events_from_the_event_is_recalculated() {
        var event1 = bffApi.createPaymentEvent(person, Instant.parse("2026-01-01T00:00:00Z"), 100);
        var event2 = bffApi.createPaymentEvent(person, Instant.parse("2026-02-01T00:00:00Z"), 110);

        assertEquals(2, timelineRepository.getTimeline(person, FetchParamenters.FullHistory).get().countSumCalculationGenerations());

        var state = bffApi.adjustPaymentEvent(person, event1.eventId(), 90);

        assertEquals(200, state.paymentsPerYear().get(2026));
        assertEquals(4, timelineRepository.getTimeline(person, FetchParamenters.FullHistory).get().countSumCalculationGenerations());
    }

    @Test
    void when_archiving_Then_all_historic_calculations_are_moved_to_history() {
        var event1 = bffApi.createPaymentEvent(person, Instant.parse("2026-01-01T00:00:00Z"), 100);
        var event2 = bffApi.createPaymentEvent(person, Instant.parse("2026-02-01T00:00:00Z"), 110);
        for (int i = 1; i < 100; i++)
            bffApi.adjustPaymentEvent(person, event1.eventId(), i);

        // precondition no historic data
        Timeline timeline = timelineRepository.getTimeline(person, FetchParamenters.FullHistory).get();
        int total2026 = timeline.getState().paymentsPerYear().get(2026);
        assertEquals(200, timeline.countSumCalculationGenerations());
        assertEquals(0, timeline.getHistoricEvents().size());

        // act
        var archiver = new CalculationGenerationsArchiver(personRepository, timelineRepository);
        int archiveCount = archiver.archiveAll();

        // assert work has been made
        assertEquals(198, archiveCount);

        // assert archiving has data
        timeline = timelineRepository.getTimeline(person, FetchParamenters.FullHistory).get();
        var history = timeline.getHistoricEvents();
        assertEquals(2, history.size());
        assertEquals(198, timeline.countSumHistoricCalculationGenerations());

        // assert simple fetch
        timeline = timelineRepository.getTimeline(person, FetchParamenters.Latest).get();
        assertEquals(2, timeline.countSumCalculationGenerations());
        assertEquals(0, timeline.countSumHistoricCalculationGenerations());

        // assert same calculation result
        int newTotal2026 = timeline.getState().paymentsPerYear().get(2026);
        assertEquals(total2026, newTotal2026);
    }
}
