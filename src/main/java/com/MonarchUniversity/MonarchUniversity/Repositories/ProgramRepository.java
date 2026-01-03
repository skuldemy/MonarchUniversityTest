package com.MonarchUniversity.MonarchUniversity.Repositories;

import java.util.List;
import java.util.Optional;

import com.MonarchUniversity.MonarchUniversity.Entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;

import com.MonarchUniversity.MonarchUniversity.Entity.Program;

public interface ProgramRepository extends JpaRepository<Program, Long> {
	List<Program> findByDepartmentId(Long id);
    boolean existsByProgramNameIgnoreCase(String programName);

    Optional<Program> findByProgramNameAndDepartment(
            String programName, Department department);

}
