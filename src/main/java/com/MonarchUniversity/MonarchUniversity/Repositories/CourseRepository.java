package com.MonarchUniversity.MonarchUniversity.Repositories;

import com.MonarchUniversity.MonarchUniversity.Entity.Course;
import com.MonarchUniversity.MonarchUniversity.Entity.Level;
import com.MonarchUniversity.MonarchUniversity.Entity.Program;
import com.MonarchUniversity.MonarchUniversity.Payload.CourseResponseDto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findAllByProgramAndLevel(Program program, Level level);

    boolean existsByCourseCodeIgnoreCaseAndLevelAndProgram(String courseCode, Level level, Program program);

    boolean existsByCourseTitleIgnoreCaseAndLevelAndProgram(String courseTitle, Level level, Program program);
}
