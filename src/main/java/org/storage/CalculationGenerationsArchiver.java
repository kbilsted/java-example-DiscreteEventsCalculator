package org.storage;

import org.models.Event;

import java.util.Optional;

/**
 * The archiver moves outdate data to another document in the document store making it
 * faster to fetch the data only relevant to most use cases.
 * <br>
 * we do this asynchronously, rather than during the save operation, to ensure we only fetch
 * historic data when really needed.
 */
public class CalculationGenerationsArchiver {
    private final PersonRepository personRepository;
    private final TimelineRepository timelineRepository;

    public CalculationGenerationsArchiver(PersonRepository personRepository, TimelineRepository timelineRepository) {
        this.personRepository = personRepository;
        this.timelineRepository = timelineRepository;
    }

    public int archiveAll() {
        int count = 0;
        Optional<Integer> id;

        while (true) {
            id = timelineRepository.getArchivableId();

            if (id.isEmpty())
                break;

            count += archiveSpecific(id.get());

            timelineRepository.storeArchivableTimeline(id.get(), false);
        }

        return count;
    }

    public int archiveSpecific(int personId) {
        var person = personRepository.getPerson(personId)
                .orElseThrow(() -> new RuntimeException("person id not found"));

        var timeline = timelineRepository.getTimeline(person, FetchParamenters.FullHistory)
                .orElseThrow(() -> new RuntimeException("No timeline found"));

        var history = timeline.getHistoricEvents();
        int archivedCount = 0;

        for (int i = 0; i < timeline.getEvents().size(); i++) {
            Event event = timeline.getEvents().get(i);

            // ensure event exist in history
            if (i >= history.size()) {
                var clone = event.deepClone();
                clone.generations().clear();
                history.add(clone);
            } else if (history.get(i).eventId() != event.eventId()) {
                var clone = event.deepClone();
                clone.generations().clear();
                history.add(i, clone);
            }

            // move all but latest generation
            Event historyEvent = history.get(i);
            var generations = event.generations();
            var latest = generations.getLast();

            for (int g = 0; g < generations.size() - 1; g++) {
                historyEvent.generations().add(generations.get(g));
                archivedCount++;
            }
            generations.clear();
            generations.add(latest);
        }

        if (!timelineRepository.storeTimeline(person, timeline, TimelineRepository.SaveOptions.None)) {
            throw new RuntimeException("Timeline was modified while archiving. Cannot archive");
        }

        return archivedCount;
    }
}
