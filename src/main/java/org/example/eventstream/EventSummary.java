package org.example.eventstream;

public final class EventSummary {
    private final Event event;
    private final EventInput latestInput;
    private final CalculationResult latestResult;

    public EventSummary(Event event, EventInput latestInput, CalculationResult latestResult) {
        this.event = event;
        this.latestInput = latestInput;
        this.latestResult = latestResult;
    }

    public Event getEvent() {
        return event;
    }

    public EventInput getLatestInput() {
        return latestInput;
    }

    public CalculationResult getLatestResult() {
        return latestResult;
    }
}
