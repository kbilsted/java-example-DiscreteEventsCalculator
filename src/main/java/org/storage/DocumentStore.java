package org.storage;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.models.Person;
import org.models.Tidslinie;

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
        List<Person> personer,
        HashMap<Integer, Tidslinie> tidslinier) {
    private static final ObjectMapper OBJECT_MAPPER = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .build();

    public DocumentStore() {
        this(new ArrayList<>(), new HashMap<>());
    }

    /*
    ai: tilføj en save metode på DocumentStore som json serialiserer felterne for hver felt gemmer i en string. den gemmer strings i filer med
    samme navn som felterne i c:\temp og returnerer en hashmap med key variabel navn og valyue json string. anvend det mest moderne og udbredte
    json lib. før kode giv oversigt på json libs jeg bør vælge imellem
     */
    public HashMap<String, String> save() {
        HashMap<String, String> serialized = new HashMap<>();
        Path targetDir = Path.of("C:\\temp");

        try {
            Files.createDirectories(targetDir);

            serialized.put("personer", toJson(personer));
            serialized.put("tidslinier", toJson(tidslinier));
            serialized.put("GlobalId", toJson(GlobalId.current()));

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
            throw new UncheckedIOException("Kunne ikke gemme DocumentStore JSON til C:\\temp", e);
        }
    }

    private static String toJson(Object value) {
        try {
            return OBJECT_MAPPER.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Kunne ikke serialisere værdi til JSON", e);
        }
    }

}
