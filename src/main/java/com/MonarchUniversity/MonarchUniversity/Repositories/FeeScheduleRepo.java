package com.MonarchUniversity.MonarchUniversity.Repositories;

import com.MonarchUniversity.MonarchUniversity.Model.Department;
import com.MonarchUniversity.MonarchUniversity.Model.FeeSchedule;
import com.MonarchUniversity.MonarchUniversity.Model.Level;
import com.MonarchUniversity.MonarchUniversity.Model.Program;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FeeScheduleRepo extends JpaRepository<FeeSchedule, Long> {
    boolean existsByLevelAndDepartment(Level level, Department department);

    Optional<FeeSchedule> findByLevelAndDepartment(Level level, Department department);
}
