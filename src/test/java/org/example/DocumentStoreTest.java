package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DocumentStoreTest {
    DocumentStore store;
    Logik logik;
Person person;

    @BeforeEach
    void setUp() {
        DocumentStore.GlobalId = 1;
        store = new DocumentStore();
        logik = new Logik(store);

        person = logik.OpretPerson("anders and");
    }

    @Test
    void kanInstantiereDocumentStoreOgIndsaettePersonOgIndbetalingMedInput() {
        logik.OpretHændelseBetaling(person, 100, Instant.parse("2026-01-01T00:00:00Z"));

        assertEquals(1, store.personer().size());
        assertEquals("indbetaling", store.tidslinier().get(person.id()).hændelser().getFirst().navn());
        assertEquals("100", store.tidslinier().get(person.id()).hændelser().getFirst().generationer().getFirst().input().inputs().get("beløb"));
    }
}
