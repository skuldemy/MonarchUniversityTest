package com.MonarchUniversity.MonarchUniversity.Repositories;

import com.MonarchUniversity.MonarchUniversity.Entity.Timetable;
import com.MonarchUniversity.MonarchUniversity.Entity.TimetableEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Arrays;
import java.util.List;

public interface TimetableEntryRepository extends JpaRepository<TimetableEntry, Long> {
    List<TimetableEntry> findByTimetableOrderByDayAscStartTimeAsc(Timetable timetable);

    List<TimetableEntry> findByTimetable(Timetable timetable);
}
