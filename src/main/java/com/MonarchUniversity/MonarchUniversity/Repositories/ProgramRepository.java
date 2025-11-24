package com.MonarchUniversity.MonarchUniversity.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.MonarchUniversity.MonarchUniversity.Entity.Program;

public interface ProgramRepository extends JpaRepository<Program, Long> {

}
