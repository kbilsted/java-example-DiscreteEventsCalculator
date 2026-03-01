package org.models;

import org.storage.DocumentStore;

public record Person(Integer id, String navn) {
    public Person(String navn) {
        this (DocumentStore.GlobalId++, navn);
    }
}
