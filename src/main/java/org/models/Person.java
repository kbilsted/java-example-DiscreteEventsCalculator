package org.models;

import org.storage.GlobalId;

public record Person(Integer id, String name) {
    public Person(String name) {
        this(GlobalId.next(), name);
    }
}
