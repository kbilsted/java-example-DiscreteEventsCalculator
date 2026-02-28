package org.example.eventstream;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class CalculationEngine {
    private static final Comparator<EventInput> LATEST_INPUT_COMPARATOR =
            Comparator.comparingLong(EventInput::getRealTime)
                    .thenComparing(EventInput::getCreatedAt)
                    .thenComparingLong(EventInput::getInputId);

    private static final Comparator<Event> EVENT_SORT_COMPARATOR =
            Comparator.comparing(Event::getValorDate)
                    .thenComparing(Event::getCreatedAt)
                    .thenComparingLong(Event::getEventId);

    private final Map<Long, Event> eventsById = new HashMap<>();
    private final Map<Long, List<EventInput>> inputsByEventId = new HashMap<>();
    private final Map<Long, List<CalculationResult>> resultsByEventId = new HashMap<>();

    public void addEvent(Event event) {
        Objects.requireNonNull(event, "event must not be null");
        Event existing = eventsById.putIfAbsent(event.getEventId(), event);
        if (existing != null) {
            throw new IllegalArgumentException("Event id already exists: " + event.getEventId());
        }
    }

    public CalculationResult addEventInput(EventInput input) {
        Objects.requireNonNull(input, "input must not be null");

        Event event = eventsById.get(input.getEventId());
        if (event == null) {
            throw new IllegalArgumentException("Unknown event id: " + input.getEventId());
        }

        List<EventInput> inputs = inputsByEventId.computeIfAbsent(input.getEventId(), key -> new ArrayList<>());
        boolean duplicateInput = inputs.stream().anyMatch(i -> i.getInputId() == input.getInputId());
        if (duplicateInput) {
            throw new IllegalArgumentException("Input id already exists for event: " + input.getInputId());
        }

        inputs.add(input);

        int generation = resultsByEventId.getOrDefault(input.getEventId(), List.of()).size() + 1;
        int aggregateAmount = inputs.stream().mapToInt(EventInput::getAmount).sum();

        CalculationResult result = new CalculationResult(
                event.getEventId(),
                input.getInputId(),
                generation,
                input.getRealTime(),
                Instant.now(),
                aggregateAmount
        );

        resultsByEventId.computeIfAbsent(input.getEventId(), key -> new ArrayList<>()).add(result);
        return result;
    }

    public List<EventSummary> listEventsForPerson(String personId) {
        Objects.requireNonNull(personId, "personId must not be null");

        return eventsById.values().stream()
                .filter(event -> personId.equals(event.getPersonId()))
                .sorted(EVENT_SORT_COMPARATOR)
                .map(this::toSummary)
                .toList();
    }

    public List<CalculationResult> getResultsForEvent(long eventId) {
        return List.copyOf(resultsByEventId.getOrDefault(eventId, List.of()));
    }

    private EventSummary toSummary(Event event) {
        List<EventInput> inputs = inputsByEventId.getOrDefault(event.getEventId(), List.of());
        List<CalculationResult> results = resultsByEventId.getOrDefault(event.getEventId(), List.of());

        EventInput latestInput = inputs.stream().max(LATEST_INPUT_COMPARATOR).orElse(null);
        CalculationResult latestResult = results.isEmpty() ? null : results.get(results.size() - 1);

        return new EventSummary(event, latestInput, latestResult);
    }
}
