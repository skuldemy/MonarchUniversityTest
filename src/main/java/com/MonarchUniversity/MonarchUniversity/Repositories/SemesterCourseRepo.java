package com.MonarchUniversity.MonarchUniversity.Repositories;

import com.MonarchUniversity.MonarchUniversity.Model.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SemesterCourseRepo extends JpaRepository<SemesterCourse, Long> {
    List<SemesterCourse> findBySemesterAndCourse_DepartmentAndCourse_Level(
            Semester semester, Department department, Level level
            );
    boolean existsByCourseAndSemester(Course course, Semester semester);

}
