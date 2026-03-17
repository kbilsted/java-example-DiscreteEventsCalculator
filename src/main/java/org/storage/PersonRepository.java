package org.storage;

import lombok.NonNull;
import org.models.Person;

import java.util.Optional;

public class PersonRepository {
    private final DocumentStore store;

    public PersonRepository(DocumentStore store) {
        this.store = store;
    }

    public Optional<Person> getPerson(int id) {
        return Optional.ofNullable(store.people.get(id));
    }

    public void storePerson(@NonNull Person person) {
        store.people.put(person.id(), person);
    }

    public int countPeople() {
        return store.people.size();
    }
}
