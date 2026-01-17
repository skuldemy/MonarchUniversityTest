package com.MonarchUniversity.MonarchUniversity.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.MonarchUniversity.MonarchUniversity.Model.Faculty;

import java.util.Optional;

public interface FacultyRepository extends JpaRepository<Faculty, Long> {
    boolean existsByFacultyNameIgnoreCase(String facultyName);

    Optional<Faculty> findByFacultyName(String facultyName);
}
