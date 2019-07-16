package io.pivotal.pal.tracker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTimeEntryRepository implements TimeEntryRepository {
    private long id = 1L;
    private Map<Long, TimeEntry> repository = new HashMap<>();

    public TimeEntry create(TimeEntry timeEntry) {
        timeEntry.setId(id);
        this.repository.put(id, timeEntry);
        id++;
        return timeEntry;
    }

    public TimeEntry find(long id) {
        return this.repository.get(id);
    }

    public List<TimeEntry> list() {
        return new ArrayList<>(this.repository.values());
    }

    public TimeEntry update(long id, TimeEntry timeEntry) {
        if (this.repository.get(id) == null) {
            return null;
        }
        timeEntry.setId(id);
        this.repository.replace(id, timeEntry);
        return timeEntry;
    }

    public void delete(long id) {
        this.repository.remove(id);
    }
}
