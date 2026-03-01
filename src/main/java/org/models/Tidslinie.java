package org.models;

import java.util.List;
import java.util.ArrayList;

public record Tidslinie(List<Hændelse> hændelser) {
    public Tidslinie() {
        this(new ArrayList<>());
    }
}
