package org.example;

import org.example.eventstream.CalculationEngine;
import org.example.eventstream.Event;
import org.example.eventstream.EventInput;

import java.time.Instant;
import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
        CalculationEngine engine = new CalculationEngine();

        Event event = new Event(
                1L,
                "Salary",
                "person-1",
                LocalDate.of(2026, 1, 31),
                Instant.parse("2026-01-01T00:00:00Z")
        );

        engine.addEvent(event);
        engine.addEventInput(new EventInput(
                10L,
                1L,
                LocalDate.of(2026, 1, 31),
                1000,
                1L,
                Instant.parse("2026-01-15T10:00:00Z")
        ));

        int result = engine.getResultsForEvent(1L).get(0).getResultAmount();
        System.out.println("Latest result: " + result);
    }
}
