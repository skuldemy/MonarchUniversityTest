package com.MonarchUniversity.MonarchUniversity.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.MonarchUniversity.MonarchUniversity.Entity.Faculty;
import com.MonarchUniversity.MonarchUniversity.Entity.Level;

public interface LevelRepository extends JpaRepository<Level, Long> {
	List<Level> findByProgramId(Long id);
}
