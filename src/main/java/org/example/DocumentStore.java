package org.example;

import java.util.HashMap;
import java.util.List;

public record DocumentStore(
        List<Person> personer,
        HashMap<Integer, Tidslinie> tidslinier) {
    public  static int GlobalId=1;

    public DocumentStore(){
        this(List.of(), new HashMap<>());
    }
}
