package org.storage;

import org.bffs.BffApi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.models.Person;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CalculationGenerationsArchiverTest {
    private DocumentStore store;
    private BffApi bffApi;
    private Person person;

    @BeforeEach
    void setUp() {
        GlobalId.reset(1);
        store = new DocumentStore();
        bffApi = new BffApi(store);
        person = bffApi.createPerson("archive person");
    }

    @Test
    void archive_copies_missing_events_and_moves_old_generations() {
        var event1 = bffApi.createPaymentEvent(person, Instant.parse("2026-01-01T00:00:00Z"), 100);
        var event2 = bffApi.createPaymentEvent(person, Instant.parse("2026-02-01T00:00:00Z"), 110);

        bffApi.adjustPaymentEvent(person, event1.eventId(), 90);

        assertEquals(4, store.getTimeline(person, FetchParamenters.Latest).countSumCalculationGenerations());
        assertEquals(0, store.getTimeline(person, FetchParamenters.FullHistory).getHistoricEvents().size());

        var archiver = new CalculationGenerationsArchiver(store);
        archiver.Archive(person.id());

        var timeline = store.getTimeline(person, FetchParamenters.FullHistory);
        var history = timeline.getHistoricEvents();

        assertEquals(2, history.size());
        assertEquals(2, history.stream().mapToInt(x -> x.generations().size()).sum());
        assertNotNull(history.getFirst().generations().getFirst().input());
        assertEquals(2, timeline.countSumCalculationGenerations());
    }
}
