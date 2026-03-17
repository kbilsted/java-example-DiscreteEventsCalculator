package org.storage;

import lombok.NonNull;
import org.models.Event;
import org.models.Person;
import org.models.Timeline;

import java.util.ArrayList;
import java.util.Optional;

public class TimelineRepository {
    private final DocumentStore store;

    public TimelineRepository(DocumentStore store) {
        this.store = store;
    }

    public enum SaveOptions {
        None, MarkForArchiving
    }

    public boolean storeTimeline(@NonNull Person person, @NonNull Timeline toStoreTimeline) {
        return storeTimeline(person, toStoreTimeline, SaveOptions.MarkForArchiving);
    }

    /**
     * store using optimistic lock
     */
    public boolean storeTimeline(@NonNull Person person, @NonNull Timeline toStoreTimeline, TimelineRepository.SaveOptions saveOptions) {
        synchronized (DocumentStore.lock) {
            Integer id = person.id();

            var lookup = store.timelines.get(id);
            if (lookup == null) {
                store.timelines.put(id, toStoreTimeline);
                return true;
            }

            if (lookup.getStoreGeneration() != toStoreTimeline.getStoreGeneration()) {
                return false;
            }

            toStoreTimeline.getFetchParamenters().ifPresent(p -> {
                if (p == FetchParamenters.FullHistory) {
                    store.historicEvents.put(id, toStoreTimeline.getHistoricEvents());
                }
            });

            // ensure we never store historic entries since they are stored in a different document
            toStoreTimeline.setHistoricEvents(new ArrayList<>());

            // optimistic lock versioning
            toStoreTimeline.setStoreGeneration(toStoreTimeline.getStoreGeneration() + 1);

            store.timelines.put(id, toStoreTimeline);

            if (saveOptions == SaveOptions.MarkForArchiving)
                store.archivableTimelines.add(id);

            return true;
        }
    }

    /**
     * fetch using fetch parameters
     */
    public Optional<Timeline> getTimeline(@NonNull Person person, @NonNull FetchParamenters parameters) {
        var personId = person.id();

        return Optional
                .ofNullable(store.timelines.get(personId))
                .map(timeline -> {
                    // deep clone to simulate fetching from real storage
                    timeline = timeline.deepClone();
                    timeline.setFetchParamenters(Optional.of(parameters));

                    switch (parameters) {
                        case FullHistory -> timeline.setHistoricEvents(getHistoricEvents(person));
                        case Latest -> {
                        }
                    }
                    return timeline;
                });
    }

    public ArrayList<Event> getHistoricEvents(@NonNull Person person) {
        var historic = store.historicEvents.computeIfAbsent(person.id(), _ -> new ArrayList<>());

        // deep clone to simulate fetching from real storage
        var clone = new ArrayList<Event>(historic.size());
        for (var event : historic) {
            clone.add(event.deepClone());
        }

        return clone;
    }

    public int countTimelines() {
        return store.timelines.size();
    }

    public Optional<Integer> getArchivableId() {
        return store.archivableTimelines.stream().findAny();
    }

    public void storeArchivableTimeline(Integer id, boolean isArchivable) {
        if (isArchivable)
            store.archivableTimelines.add(id);
        else
            store.archivableTimelines.remove(id);
    }
}
