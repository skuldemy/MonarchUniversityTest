package com.MonarchUniversity.MonarchUniversity.Repositories;

import com.MonarchUniversity.MonarchUniversity.Model.Course;
import com.MonarchUniversity.MonarchUniversity.Model.Department;
import com.MonarchUniversity.MonarchUniversity.Model.Level;
import com.MonarchUniversity.MonarchUniversity.Model.Program;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findAllByDepartmentAndLevel(Department department, Level level);

    boolean existsByCourseCodeIgnoreCaseAndLevelAndDepartment(String courseCode, Level level, Department department);

    boolean existsByCourseTitleIgnoreCaseAndLevelAndDepartment(String courseTitle, Level level, Department department);
}
