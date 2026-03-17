package org.models;

import lombok.NonNull;

import java.time.Instant;
import java.util.HashMap;

public record EventInput(int inputId, Instant createTime, @NonNull HashMap<String, Object> inputs) {
    public EventInput deepClone() {
        return new EventInput(inputId, createTime, new HashMap<>(inputs));
    }
}
