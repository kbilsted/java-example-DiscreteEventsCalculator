package org.bffs;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.models.Person;
import org.storage.DocumentStore;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DocumentStoreTest {
    DocumentStore store;
    BffApi bffApi;
    Person person;

    @BeforeEach
    void setUp() {
        DocumentStore.GlobalId = 1;
        store = new DocumentStore();
        bffApi = new BffApi(store);

        person = bffApi.OpretPerson("anders and");
    }

    @Test
    void kanInstantiereDocumentStoreOgIndsaettePersonOgIndbetalingMedInput() {
        bffApi.OpretHændelseBetaling(person, Instant.parse("2026-01-01T00:00:00Z"), 100);

        assertEquals(1, store.personer().size());
        assertEquals("indbetaling", store.tidslinier().get(person.id()).hændelser().getFirst().navn());
        assertEquals(100, store.tidslinier().get(person.id()).hændelser().getFirst().generationer().getFirst().input().inputs().get("beløb"));
    }
}
