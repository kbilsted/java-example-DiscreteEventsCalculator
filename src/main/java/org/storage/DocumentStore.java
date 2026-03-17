package org.storage;

import org.models.Event;
import org.models.Person;
import org.models.Timeline;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/** simulate a storage device without transactions */
public class DocumentStore {
    final HashMap</* person*/ Integer, Person> people = new HashMap<>();
    final HashSet</* person*/ Integer> archivableTimelines = new HashSet<>();
    final HashMap</* person */Integer, Timeline> timelines = new HashMap<>();
    final HashMap</* person */ Integer, ArrayList<Event>> historicEvents = new HashMap<>();
    final static Object lock = new Object();
}
