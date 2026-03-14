package com.MonarchUniversity.MonarchUniversity.Repositories;

import com.MonarchUniversity.MonarchUniversity.Model.CourseAssessmentStructure;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CourseAssessmentRepo extends JpaRepository<CourseAssessmentStructure, Long> {
    Optional<CourseAssessmentStructure> findBySemesterCourse_Id(Long semesterCourseId);
}
