package org.example.eventstream;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CalculationEngineTest {

    @Test
    void addEventInputIncrementsGenerationAndAggregatesAmount() {
        CalculationEngine engine = new CalculationEngine();
        engine.addEvent(new Event(1L, "Salary", "person-1", LocalDate.of(2026, 1, 31), Instant.parse("2026-01-01T00:00:00Z")));

        CalculationResult first = engine.addEventInput(new EventInput(
                1L,
                1L,
                LocalDate.of(2026, 1, 31),
                100,
                1L,
                Instant.parse("2026-01-10T00:00:00Z")
        ));

        CalculationResult second = engine.addEventInput(new EventInput(
                2L,
                1L,
                LocalDate.of(2026, 1, 31),
                25,
                2L,
                Instant.parse("2026-01-11T00:00:00Z")
        ));

        assertEquals(1, first.getGeneration());
        assertEquals(100, first.getResultAmount());
        assertEquals(2, second.getGeneration());
        assertEquals(125, second.getResultAmount());
    }

    @Test
    void listEventsForPersonIsSortedByValorDateAndShowsLatestInputAndResult() {
        CalculationEngine engine = new CalculationEngine();

        engine.addEvent(new Event(2L, "Bonus", "person-1", LocalDate.of(2026, 2, 1), Instant.parse("2026-01-01T00:00:00Z")));
        engine.addEvent(new Event(1L, "Salary", "person-1", LocalDate.of(2026, 1, 1), Instant.parse("2026-01-01T00:00:00Z")));

        engine.addEventInput(new EventInput(
                10L,
                1L,
                LocalDate.of(2026, 1, 1),
                100,
                1L,
                Instant.parse("2026-01-10T00:00:00Z")
        ));
        engine.addEventInput(new EventInput(
                11L,
                1L,
                LocalDate.of(2026, 1, 1),
                10,
                2L,
                Instant.parse("2026-01-11T00:00:00Z")
        ));

        List<EventSummary> summaries = engine.listEventsForPerson("person-1");

        assertEquals(2, summaries.size());
        assertEquals(1L, summaries.get(0).getEvent().getEventId());
        assertEquals(2L, summaries.get(1).getEvent().getEventId());
        assertEquals(11L, summaries.get(0).getLatestInput().getInputId());
        assertEquals(110, summaries.get(0).getLatestResult().getResultAmount());
    }

    @Test
    void addEventInputFailsForUnknownEvent() {
        CalculationEngine engine = new CalculationEngine();

        assertThrows(IllegalArgumentException.class, () -> engine.addEventInput(new EventInput(
                10L,
                999L,
                LocalDate.of(2026, 1, 1),
                100,
                1L,
                Instant.parse("2026-01-10T00:00:00Z")
        )));
    }

    @Test
    void addEventInputFailsForDuplicateInputIdWithinEvent() {
        CalculationEngine engine = new CalculationEngine();
        engine.addEvent(new Event(1L, "Salary", "person-1", LocalDate.of(2026, 1, 31), Instant.parse("2026-01-01T00:00:00Z")));

        engine.addEventInput(new EventInput(
                10L,
                1L,
                LocalDate.of(2026, 1, 31),
                100,
                1L,
                Instant.parse("2026-01-10T00:00:00Z")
        ));

        assertThrows(IllegalArgumentException.class, () -> engine.addEventInput(new EventInput(
                10L,
                1L,
                LocalDate.of(2026, 1, 31),
                50,
                2L,
                Instant.parse("2026-01-11T00:00:00Z")
        )));
    }
}
