package com.MonarchUniversity.MonarchUniversity.Repositories;

import com.MonarchUniversity.MonarchUniversity.Entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long> {
}
