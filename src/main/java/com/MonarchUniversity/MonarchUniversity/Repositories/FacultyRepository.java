package com.MonarchUniversity.MonarchUniversity.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.MonarchUniversity.MonarchUniversity.Entity.Faculty;

public interface FacultyRepository extends JpaRepository<Faculty, Long> {

}
