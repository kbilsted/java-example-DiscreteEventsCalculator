package org.bffs;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.models.Person;
import org.storage.DocumentStore;
import org.storage.GlobalId;

import java.time.Instant;

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
        assertEquals("payment", store.timelines().get(person.id()).events().getFirst().name());
        assertEquals(100, store.timelines().get(person.id()).events().getFirst().generations().getFirst().input().inputs().get("amount"));
    }
}
