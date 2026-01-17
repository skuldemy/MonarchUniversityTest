package com.MonarchUniversity.MonarchUniversity.Repositories;

import com.MonarchUniversity.MonarchUniversity.Model.Course;
import com.MonarchUniversity.MonarchUniversity.Model.Level;
import com.MonarchUniversity.MonarchUniversity.Model.Program;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findAllByProgramAndLevel(Program program, Level level);

    boolean existsByCourseCodeIgnoreCaseAndLevelAndProgram(String courseCode, Level level, Program program);

    boolean existsByCourseTitleIgnoreCaseAndLevelAndProgram(String courseTitle, Level level, Program program);
}
