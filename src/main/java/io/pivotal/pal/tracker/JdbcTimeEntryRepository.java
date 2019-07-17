package io.pivotal.pal.tracker;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Date;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

import static java.lang.String.format;

public class JdbcTimeEntryRepository implements TimeEntryRepository {
    private final JdbcTemplate jdbcTemplate;

    public JdbcTimeEntryRepository(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public TimeEntry create(TimeEntry timeEntry) {
        HashMap<String, Object> insert = new HashMap<>();
        insert.put("project_id", timeEntry.getProjectId());
        insert.put("user_id", timeEntry.getUserId());
        insert.put("date", timeEntry.getDate().format(DateTimeFormatter.ofPattern("YYYY-MM-DD")));
        insert.put("hours", timeEntry.getHours());

        long id = new SimpleJdbcInsert(this.jdbcTemplate)
                .usingColumns("project_id", "user_id", "date", "hours")
                .withTableName("time_entries")
                .usingGeneratedKeyColumns("id")
                .executeAndReturnKey(insert).longValue();

        timeEntry.setId(id);

        return timeEntry;
    }

    @Override
    public TimeEntry find(long timeEntryId) {
        return this.jdbcTemplate.query(
                format("SELECT * FROM time_entries WHERE id = %s;", timeEntryId),
                (ResultSet rs) -> {
                    if (rs == null) {
                        return null;
                    }
                    if (rs.next()) {
                        long id = rs.getLong("id");
                        LocalDate date = rs.getDate("date").toLocalDate();
                        long projectId = rs.getLong("project_id");
                        long userId = rs.getLong("user_id");
                        int hours = rs.getInt("hours");
                        return new TimeEntry(id, projectId, userId, date, hours);
                    }
                    return null;
                }
        );
    }

    @Override
    public List<TimeEntry> list() {
        ArrayList<TimeEntry> timeEntries = new ArrayList<>();
        this.jdbcTemplate.query(
                "SELECT * FROM time_entries;",
                (ResultSet rs) -> {
                    if (rs == null) {
                        return null;
                    }
                    while (rs.next()) {
                        long id = rs.getLong("id");
                        LocalDate date = rs.getDate("date").toLocalDate();
                        long projectId = rs.getLong("project_id");
                        long userId = rs.getLong("user_id");
                        int hours = rs.getInt("hours");
                        TimeEntry timeEntry = new TimeEntry(id, projectId, userId, date, hours);
                        timeEntries.add(timeEntry);
                    }
                    return null;
                }
        );
        return timeEntries;
    }

    @Override
    public TimeEntry update(long id, TimeEntry timeEntry) {
        this.jdbcTemplate.execute(format(
                "UPDATE time_entries " +
                        "SET date = '%s' " +
                        ", project_id = %s " +
                        ", user_id = %s " +
                        ", hours = %s " +
                        "WHERE id = %s;",
                timeEntry.getDate().format(DateTimeFormatter.ofPattern("YYYY-MM-DD")),
                timeEntry.getProjectId(),
                timeEntry.getUserId(),
                timeEntry.getHours(),
                id
        ));

        timeEntry.setId(id);

        return timeEntry;
    }

    @Override
    public void delete(long timeEntryId) {
        this.jdbcTemplate.execute(format("DELETE FROM time_entries WHERE id = %s;", timeEntryId));
    }
}
