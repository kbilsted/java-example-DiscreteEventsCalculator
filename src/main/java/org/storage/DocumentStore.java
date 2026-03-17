package org.storage;

import lombok.NonNull;
import org.models.Event;
import org.models.Person;
import org.models.Timeline;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

/** simulate a storage device without transactions */
public class DocumentStore {
    private final HashMap</* person*/ Integer, Person> people = new HashMap<>();
    private final HashMap</* person */Integer, Timeline> timelines = new HashMap<>();
    private final HashMap</* person */ Integer, ArrayList<Event>> historicEvents = new HashMap<>();
    private static final Object lock = new Object();

    /**
     * store using optimistic lock
     */
    public boolean storeTimeline(@NonNull Person person, @NonNull Timeline toStoreTimeLine) {
        synchronized (lock) {
            Integer id = person.id();

            var lookup = timelines.get(id);
            if (lookup == null) {
                timelines.put(id, toStoreTimeLine);
                return true;
            }

            if (lookup.getStoreGeneration() != toStoreTimeLine.getStoreGeneration())
                return false;

            historicEvents.put(id, toStoreTimeLine.getHistoricEvents());
            toStoreTimeLine.setHistoricEvents(new ArrayList<>());

            toStoreTimeLine.setStoreGeneration(toStoreTimeLine.getStoreGeneration() + 1);
            timelines.put(id, toStoreTimeLine);
            return true;
        }
    }

    /**
     * fetch using fetch parameters
     */
    public Optional<Timeline> getTimeline(@NonNull Person person, @NonNull FetchParamenters parameters) {
        var personId = person.id();

        return Optional
                .ofNullable(timelines.get(personId))
                .map(timeline -> {
                       // deep clone to simulate fetching from real storage
                        timeline = timeline.deepClone();

                        switch (parameters) {
                            case FullHistory -> timeline.setHistoricEvents(getHistoricEvents(person));
                            case Latest -> {}
                            }
                        return timeline;
                    });
    }

    public ArrayList<Event> getHistoricEvents(Person person){
        var historic = historicEvents.computeIfAbsent(person.id(), _ -> new ArrayList<>());

        // deep clone to simulate fetching from real storage
        var clone = new ArrayList<Event>(historic.size());
        for(var e : historic)
            clone.add(e.deepClone());

        return clone;
    }

    public int countTimelines() {
        return timelines.size();
    }

    public Optional<Person> getPerson(int id) {
        return Optional.ofNullable(people.get(id));
    }

    public void storePerson(@NonNull Person p) {
        people.put(p.id(), p);
    }

    public int countPeople() {
        return people.size();
    }
}
