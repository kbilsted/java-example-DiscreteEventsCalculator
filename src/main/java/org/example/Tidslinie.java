package org.example;

import java.util.List;

public record Tidslinie(List<Hændelse> hændelser) {
    public Tidslinie() {
        this(List.of());
    }
}
