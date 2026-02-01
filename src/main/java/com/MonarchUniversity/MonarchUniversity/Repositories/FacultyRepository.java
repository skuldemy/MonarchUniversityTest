package com.MonarchUniversity.MonarchUniversity.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.MonarchUniversity.MonarchUniversity.Model.Faculty;

import java.util.Optional;

public interface FacultyRepository extends JpaRepository<Faculty, Long> {
    boolean existsByFacultyNameIgnoreCase(String facultyName);
    boolean existsByFacultyCodeIgnoreCase(String facultyCode);
    boolean existsByFacultyEmailIgnoreCase(String facultyCode);
    boolean existsByFacultyNameIgnoreCaseAndIdNot(String facultyName, Long id);
    boolean existsByFacultyCodeIgnoreCaseAndIdNot(String facultyCode, Long id);
    boolean existsByFacultyEmailIgnoreCaseAndIdNot(String facultyEmail, Long id);


    Optional<Faculty> findByFacultyName(String facultyName);
}
