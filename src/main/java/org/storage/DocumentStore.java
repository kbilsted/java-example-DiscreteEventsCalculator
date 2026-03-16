package org.storage;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.NonNull;
import org.models.Event;
import org.models.Person;
import org.models.Timeline;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

/** simulate a storage device without transactions */
public class DocumentStore {
    private static final ObjectMapper OBJECT_MAPPER = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .build()
            .setVisibility(com.fasterxml.jackson.annotation.PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

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
                // deep clone to simulate fetching from real storage
                .map(timeline -> {
                        timeline = deepClone(timeline);

                        switch (parameters) {
                            case FullHistory -> {
                                var historic = historicEvents.computeIfAbsent(personId, _ -> new ArrayList<>());
                                var clonedEvents = deepClone(historic);
                                timeline.setHistoricEvents(clonedEvents);
                            }
                            case Latest -> {}
                            }
                        return timeline;
                    });
    }

    public int countTimelines() {
        return timelines.size();
    }

    private Timeline deepClone(Timeline t){
        return new Timeline(t.getId(), t.getStoreGeneration(), deepClone(t.getEvents()), deepClone(t.getHistoricEvents()));
    }

    private ArrayList<Event> deepClone(List<Event> events)  {
        ArrayList<Event> clone = new ArrayList<>(events.size());
        for (Event event : events) {
            try {
                clone.add(OBJECT_MAPPER.readValue(OBJECT_MAPPER.writeValueAsString(event), event.getClass()));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
        return clone;
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
