package com.MonarchUniversity.MonarchUniversity.Repositories;

import java.util.List;
import java.util.Optional;

import com.MonarchUniversity.MonarchUniversity.Model.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentProfileRepo extends JpaRepository<StudentProfile, Long> {
	Optional<StudentProfile> findByMatricNumber(String matricNumber);

	boolean existsByEmailAddress(String emailAddress);

    List<StudentProfile> findByDepartmentAndLevel(Department department, Level level);

    Optional<StudentProfile> findByUser(User user);
}
