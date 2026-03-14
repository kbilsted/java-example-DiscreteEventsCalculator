package org.storage;

import lombok.NonNull;
import org.models.Event;
import org.models.Person;
import org.models.Timeline;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class DocumentStore {
    private final List<Person> people = new ArrayList<>();
    private final HashMap</* person */Integer, Timeline> timelines = new HashMap<>();
    private final HashMap</* person */ Integer, List<Event>> historicEvents = new HashMap<>();

    public Person getPerson(int id) {
        return people.stream()
                .filter(x -> x.id() == id)   // eller Objects.equals(x.id(), id)
                .findFirst()
                .orElse(null);
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

    public Timeline getTimeline(@NonNull Person person, @NonNull FetchParamenters parameters) {
        var personId = person.id();

        var timeLine = timelines.get(personId);

        return switch (parameters) {
            case FullHistory -> {
                var historic = historicEvents.computeIfAbsent(personId, k -> new ArrayList<>());
                timeLine.setHistoricEvents(historic);
                yield timeLine;
            }
            case Latest -> {
                timeLine.setHistoricEvents(new ArrayList<>());
                yield timeLine;
            }
        };
    }

    public int countTimelines() {
        return timelines.size();
    }

}
