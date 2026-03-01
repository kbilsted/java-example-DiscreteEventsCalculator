package org.models;

import org.storage.GlobalId;

public record Person(Integer id, String navn) {
    public Person(String navn) {
        this(GlobalId.next(), navn);
    }
}
