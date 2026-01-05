package com.MonarchUniversity.MonarchUniversity.Repositories;

import java.util.List;
import java.util.Optional;

import com.MonarchUniversity.MonarchUniversity.Entity.Level;
import com.MonarchUniversity.MonarchUniversity.Entity.Program;
import com.MonarchUniversity.MonarchUniversity.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import com.MonarchUniversity.MonarchUniversity.Entity.StudentProfile;

public interface StudentProfileRepo extends JpaRepository<StudentProfile, Long> {
	Optional<StudentProfile> findByMatricNumber(String matricNumber);

	boolean existsByEmailAddress(String emailAddress);

    List<StudentProfile> findByProgramAndLevel(Program program, Level level);

    Optional<StudentProfile> findByUser(User user);
}
