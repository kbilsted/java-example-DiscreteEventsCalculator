package org.bffs;

import org.models.*;
import org.storage.DocumentStore;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class BffApi {
    private final DocumentStore store;

    public BffApi(DocumentStore store) {
        this.store = store;
    }

    public Person OpretPerson(String navn){
        var person = new Person(DocumentStore.GlobalId++, navn);
        store.personer().add(person);

        var tidslinie = new Tidslinie();
        store.tidslinier().put(person.id(), tidslinie);

        return person;
    }

    public Hændelse OpretHændelseBetaling(Person person, Instant valør, int beløb) {
        Hændelse hændelse = new Hændelse("indbetaling", valør, Instant.now());
        HændelseInput input = new HændelseInput(DocumentStore.GlobalId++, Instant.now(),  new HashMap<>(Map.of("beløb", beløb)));

        Tidslinie tidslinie = store.tidslinier().get(person.id());
        tidslinie.Add(hændelse, input);

        return hændelse;
    }
}
