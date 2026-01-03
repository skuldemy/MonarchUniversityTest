package com.MonarchUniversity.MonarchUniversity.Repositories;

import java.util.List;
import java.util.Optional;

import com.MonarchUniversity.MonarchUniversity.Entity.Faculty;
import org.springframework.data.jpa.repository.JpaRepository;

import com.MonarchUniversity.MonarchUniversity.Entity.Department;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
	List<Department> findByFacultyId(Long id);
    boolean existsByDepartmentNameIgnoreCase(String departmentName);

    Optional<Department> findByDepartmentNameAndFaculty(
            String departmentName, Faculty faculty);

}
