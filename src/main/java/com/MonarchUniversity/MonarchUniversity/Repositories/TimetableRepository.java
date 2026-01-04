package com.MonarchUniversity.MonarchUniversity.Repositories;

import com.MonarchUniversity.MonarchUniversity.Entity.Level;
import com.MonarchUniversity.MonarchUniversity.Entity.Program;
import com.MonarchUniversity.MonarchUniversity.Entity.Timetable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TimetableRepository extends JpaRepository<Timetable, Long> {
    Optional<Timetable> findByProgramAndLevelAndSemesterAndAcademicYear(Program program, Level level, String semester, Integer academicYear);
}
