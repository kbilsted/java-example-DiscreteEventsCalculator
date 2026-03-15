package org.models;

import lombok.NonNull;

import java.time.Instant;

public record CalculationGeneration(@NonNull Instant createTime, @NonNull EventInput input, @NonNull State state) {
}
