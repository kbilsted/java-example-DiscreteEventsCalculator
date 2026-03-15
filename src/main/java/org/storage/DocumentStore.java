package org.storage;

import lombok.NonNull;
import org.models.Event;
import org.models.Person;
import org.models.Timeline;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class DocumentStore {
    private final HashMap</* person*/ Integer, Person> people = new HashMap<>();
    private final HashMap</* person */Integer, Timeline> timelines = new HashMap<>();
    private final HashMap</* person */ Integer, List<Event>> historicEvents = new HashMap<>();
    private static final Object lock = new Object();

    public Optional<Person> getPerson(int id) {
        return Optional.ofNullable(people.get(id));
    }

    public void storePerson(@NonNull Person p) {
        people.put(p.id(), p);
    }

    public int countPeople() {
        return people.size();
    }

    /** store using optimistic lock */
    public boolean storeTimeline(@NonNull Person person, @NonNull Timeline t) {
        synchronized (lock) {
            Integer id = person.id();

            if (!timelines.containsKey(id)) {
                timelines.put(id, t);
                return true;
            }

            if (timelines.get(id).getStoreGeneration() == t.getStoreGeneration()) {
                t.setStoreGeneration(t.getStoreGeneration() + 1);
                timelines.put(id, t);
                return true;
            }

            return false;
        }
    }

    /** fetch using fetch parameters */
    public Optional<Timeline> getTimeline(@NonNull Person person, @NonNull FetchParamenters parameters) {
        var personId = person.id();

        return Optional
                .ofNullable(timelines.get(personId))
                .map(timeLine -> {
                    switch (parameters) {
                        case FullHistory -> {
                            var historic = historicEvents.computeIfAbsent(personId, _ -> new ArrayList<>());
                            timeLine.setHistoricEvents(historic);
                        }
                        case Latest -> { }
                    }
                    return timeLine;
                });
    }

    public int countTimelines() {
        return timelines.size();
    }
}
