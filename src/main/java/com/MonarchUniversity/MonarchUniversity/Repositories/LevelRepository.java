package com.MonarchUniversity.MonarchUniversity.Repositories;

import java.util.List;
import java.util.Optional;

import com.MonarchUniversity.MonarchUniversity.Model.Program;
import org.springframework.data.jpa.repository.JpaRepository;

import com.MonarchUniversity.MonarchUniversity.Model.Level;

public interface LevelRepository extends JpaRepository<Level, Long> {
	List<Level> findByProgramId(Long id);

    Optional<Level> findByLevelNumberAndProgram(String levelNumber, Program program);
    Optional<Level> findByIdAndProgram(Long levelId, Program program);
}
