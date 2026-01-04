package com.MonarchUniversity.MonarchUniversity.Repositories;

import com.MonarchUniversity.MonarchUniversity.Entity.FeeSchedule;
import com.MonarchUniversity.MonarchUniversity.Entity.Level;
import com.MonarchUniversity.MonarchUniversity.Entity.Program;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FeeScheduleRepo extends JpaRepository<FeeSchedule, Long> {
    boolean existsByLevelAndProgram(Level level, Program program);

    Optional<FeeSchedule> findByLevelAndProgram(Level level, Program program);
}
