package org.models;

import lombok.NonNull;
import org.storage.GlobalId;

public record Person(Integer id, String name) {
    public Person(@NonNull String name) {
        this(GlobalId.next(), name);
    }
}
