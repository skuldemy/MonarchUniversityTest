package com.MonarchUniversity.MonarchUniversity.Repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.MonarchUniversity.MonarchUniversity.Entity.StudentProfile;

public interface StudentProfileRepo extends JpaRepository<StudentProfile, Long> {
	Optional<StudentProfile> findByMatricNumber(String matricNumber);

	boolean existsByEmailAddress(String emailAddress);
}
