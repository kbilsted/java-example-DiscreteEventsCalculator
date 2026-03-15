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
    private final List<Person> people = new ArrayList<>();
    private final HashMap</* person */Integer, Timeline> timelines = new HashMap<>();
    private final HashMap</* person */ Integer, List<Event>> historicEvents = new HashMap<>();

    public Optional<Person> getPerson(int id) {
        return people.stream()
                .filter(x -> x.id() == id)   // eller Objects.equals(x.id(), id)
                .findFirst();
    }

    public int countPeople() {
        return people.size();
    }

    public void addPerson(@NonNull Person p) {
        people.add(p);
    }

    public void addTimeline(@NonNull Person person, Timeline t) {
        timelines.put(person.id(), t);
    }

    public Optional<Timeline> getTimeline(@NonNull Person person, @NonNull FetchParamenters parameters) {
        var personId = person.id();

        return Optional.ofNullable(timelines.get(personId))
                .map(timeLine -> {
                    switch (parameters) {
                        case FullHistory -> {
                            var historic = historicEvents.computeIfAbsent(personId, _ -> new ArrayList<>());
                            timeLine.setHistoricEvents(historic);
                        }
                        case Latest -> timeLine.setHistoricEvents(new ArrayList<>());
                    }
                    return timeLine;
                });
    }

    public int countTimelines() {
        return timelines.size();
    }
}
