package org.storage;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.NonNull;
import org.models.Event;
import org.models.Person;
import org.models.Timeline;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class DocumentStore {
    List<Person> people = new ArrayList<>();
    HashMap</* person */Integer, Timeline> timelines = new HashMap<>();
    HashMap</* person */ Integer, List<Event>> historicEvents = new HashMap<>();

    private static final ObjectMapper OBJECT_MAPPER = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .build();

    public Person getPerson(int id) {
        return people.stream()
                .filter(x -> x.id() == id)   // eller Objects.equals(x.id(), id)
                .findFirst()
                .orElse(null);
    }

    public int countPeople(){ return people.size();}

    public void addPerson(Person p){ people.add(p);}

    public void addTimeline(Person person, Timeline t){ timelines.put(person.id(), t);}

    public Timeline getTimeline(Person person, @NonNull FetchParamenters parameters) {
        var personId = person.id();

        var timeLine = timelines.get(personId);

        return switch (parameters) {
            case FullHistory -> {
                var historic = historicEvents.get(personId);
                timeLine.setHistoricEvents(historic);
                yield timeLine;
            }
            case Latest -> {
                yield timeLine;
            }
        };
    }

    public int countTimelines(){ return timelines.size();}

    public @NonNull HashMap<String, String> save() {
        HashMap<String, String> serialized = new HashMap<>();
        Path targetDir = Path.of("C:\\temp");

        try {
            Files.createDirectories(targetDir);

            serialized.put("people", toJson(people));
            serialized.put("timelines", toJson(timelines));
            serialized.put("historicEvents", toJson(historicEvents));
            serialized.put("globalId", toJson(GlobalId.current()));

            for (var entry : serialized.entrySet()) {
                Path file = targetDir.resolve(entry.getKey() + ".json");
                Files.writeString(
                        file,
                        entry.getValue(),
                        StandardCharsets.UTF_8,
                        StandardOpenOption.CREATE,
                        StandardOpenOption.TRUNCATE_EXISTING
                );
            }

            return serialized;
        } catch (IOException e) {
            throw new UncheckedIOException("Could not save DocumentStore JSON to C:\\temp", e);
        }
    }

    private static String toJson(Object value) {
        try {
            return OBJECT_MAPPER.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Could not serialize value to JSON", e);
        }
    }

}
