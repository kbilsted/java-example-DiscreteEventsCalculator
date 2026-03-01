package org.models;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public record DocumentStore(
        List<Person> personer,
        HashMap<Integer, Tidslinie> tidslinier) {
    public  static int GlobalId=1;

    public DocumentStore(){
        this(new ArrayList<>(), new HashMap<>());
    }
}
