package org.models;

public record Person(Integer id, String navn) {
    public Person(String navn) {
        this (DocumentStore.GlobalId++, navn);
    }
}
