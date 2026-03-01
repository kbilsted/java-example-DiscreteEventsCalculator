package org.bffs;

import org.models.*;

import java.time.Instant;
import java.util.HashMap;

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

    public Hændelse OpretHændelseBetaling(Person person, int beløb, Instant valør) {
        HashMap<String, Object> inputMap = new HashMap<>();
        inputMap.put("beløb", String.valueOf(beløb));
        HændelseInput input = new HændelseInput(DocumentStore.GlobalId++, Instant.now(), inputMap);

        Hændelse hændelse = new Hændelse("indbetaling", valør, Instant.now());
        hændelse.AddInput(input);

        store.tidslinier().get(person.id()).hændelser().add(hændelse);

        return hændelse;
    }
}
