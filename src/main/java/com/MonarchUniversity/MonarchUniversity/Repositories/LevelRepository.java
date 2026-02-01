package com.MonarchUniversity.MonarchUniversity.Repositories;

import java.util.List;
import java.util.Optional;

import com.MonarchUniversity.MonarchUniversity.Model.Department;
import com.MonarchUniversity.MonarchUniversity.Model.Program;
import org.springframework.data.jpa.repository.JpaRepository;

import com.MonarchUniversity.MonarchUniversity.Model.Level;

public interface LevelRepository extends JpaRepository<Level, Long> {
	List<Level> findByDepartmentId(Long id);

    Optional<Level> findByLevelNumberAndDepartment(String levelNumber, Department department);
    boolean existsByLevelNumberAndDepartment(String levelNumber, Department department);
    boolean existsByLevelNumberAndDepartmentAndIdNot(String levelNumber, Department department, Long id);
    Optional<Level> findByIdAndDepartment(Long levelId, Department department);
    List<Level> findByDepartment(Department department);
}
