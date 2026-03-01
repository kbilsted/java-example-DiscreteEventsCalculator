package org.models;

import java.time.Instant;
import java.util.HashMap;

public record EventInput(int inputId, Instant realTime, HashMap<String, Object> inputs) {
}
