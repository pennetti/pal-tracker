package io.pivotal.pal.tracker;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class TimeEntryController {
    private TimeEntryRepository timeEntryRepository;
    private final DistributionSummary distributionSummary;
    private final Counter actionCounter;

    public TimeEntryController(
            TimeEntryRepository timeEntryRepository,
            MeterRegistry meterRegistry
    ) {
        this.timeEntryRepository = timeEntryRepository;

        this.distributionSummary = meterRegistry.summary("timeEntry.summary");
        this.actionCounter = meterRegistry.counter("timeEntry.actionCounter");
    }

    @PostMapping("/time-entries")
    public ResponseEntity<TimeEntry> create(@RequestBody TimeEntry timeEntryToCreate) {
        TimeEntry timeEntry = this.timeEntryRepository.create(timeEntryToCreate);
        this.actionCounter.increment();
        this.distributionSummary.record(this.timeEntryRepository.list().size());

        return ResponseEntity.status(HttpStatus.CREATED).body(timeEntry);
    }

    @GetMapping("/time-entries/{id}")
    public ResponseEntity<TimeEntry> read(@PathVariable("id") long timeEntryId) {
        TimeEntry timeEntry = this.timeEntryRepository.find(timeEntryId);
        if (timeEntry == null) {
            return ResponseEntity.notFound().build();
        }

        this.actionCounter.increment();
        return ResponseEntity.ok(timeEntry);
    }

    @GetMapping("/time-entries")
    public ResponseEntity<List<TimeEntry>> list() {
        this.actionCounter.increment();
        return ResponseEntity.ok(this.timeEntryRepository.list());
    }

    @PutMapping("/time-entries/{id}")
    public ResponseEntity<TimeEntry> update(@PathVariable("id") long timeEntryId, @RequestBody TimeEntry expected) {
        TimeEntry timeEntry = this.timeEntryRepository.update(timeEntryId, expected);
        if (timeEntry == null) {
            return ResponseEntity.notFound().build();
        }

        this.actionCounter.increment();
        return ResponseEntity.ok(timeEntry);
    }

    @DeleteMapping("/time-entries/{id}")
    public ResponseEntity delete(@PathVariable("id") long timeEntryId) {
        this.timeEntryRepository.delete(timeEntryId);
        this.actionCounter.increment();
        this.distributionSummary.record(this.timeEntryRepository.list().size());

        return ResponseEntity.noContent().build();
    }
}
