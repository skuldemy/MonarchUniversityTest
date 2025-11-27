package com.MonarchUniversity.MonarchUniversity.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.MonarchUniversity.MonarchUniversity.Entity.Department;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
	List<Department> findByFacultyId(Long id);
}
