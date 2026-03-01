package org.storage;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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

public record DocumentStore(
        List<Person> people,
        HashMap<Integer, Timeline> timelines) {
    private static final ObjectMapper OBJECT_MAPPER = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .build();

    public DocumentStore() {
        this(new ArrayList<>(), new HashMap<>());
    }

    public HashMap<String, String> save() {
        HashMap<String, String> serialized = new HashMap<>();
        Path targetDir = Path.of("C:\\temp");

        try {
            Files.createDirectories(targetDir);

            serialized.put("people", toJson(people));
            serialized.put("timelines", toJson(timelines));
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
