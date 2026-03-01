package org.bffs;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.models.Event;
import org.models.Person;
import org.storage.DocumentStore;
import org.storage.GlobalId;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
    void createsPersonAndPaymentEventWithInput() {
        bffApi.createPaymentEvent(person, Instant.parse("2026-01-01T00:00:00Z"), 100);

        assertEquals(1, store.people().size());
        assertEquals(1, store.timelines().size());
        assertEquals("payment", store.timelines().get(person.id()).events().getLast().name());
        assertEquals(100, store.timelines().get(person.id()).events().getLast().generations().getLast().input().inputs().get("amount"));
    }

    @Test
    void createsPersonAndPaymentsEventWithInput() {
        bffApi.createPaymentEvent(person, Instant.parse("2026-01-01T00:00:00Z"), 100);
        bffApi.createPaymentEvent(person, Instant.parse("2026-02-01T00:00:00Z"), 110);

        List<Event> events = store.timelines().get(person.id()).events();
        assertEquals(2, events.size());
        HashMap<Integer, Integer> paymentsPerYear = events.getLast().generations().getLast().state().paymentsPerYear();
        assertEquals(null, paymentsPerYear.get(2025));
        assertEquals(210, paymentsPerYear.get(2026));
    }
}
