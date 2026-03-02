package org.storage;

public class CalculationGenerationsArchiver {
    private DocumentStore store;

    CalculationGenerationsArchiver(DocumentStore store) {
        this.store = store;
    }

    public void Archive(int personId) {
        var person = store.getPerson(personId);
        var timeline = store.getTimeline(person, FetchParamenters.FullHistory);


    }
}

