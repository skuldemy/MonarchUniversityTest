package com.MonarchUniversity.MonarchUniversity.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.MonarchUniversity.MonarchUniversity.Entity.Program;

public interface ProgramRepository extends JpaRepository<Program, Long> {
	List<Program> findByDepartmentId(Long id);
}
