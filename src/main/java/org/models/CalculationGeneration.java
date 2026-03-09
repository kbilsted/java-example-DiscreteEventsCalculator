package org.models;

import lombok.NonNull;

public record CalculationGeneration(@NonNull EventInput input, @NonNull State state) {
}
