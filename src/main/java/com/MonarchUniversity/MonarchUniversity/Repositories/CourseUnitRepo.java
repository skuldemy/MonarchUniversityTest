package com.MonarchUniversity.MonarchUniversity.Repositories;

import com.MonarchUniversity.MonarchUniversity.Model.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseUnitRepo extends JpaRepository<CourseUnit, Long> {
    CourseUnit getCourseUnitByDepartmentAndLevelAndSemesterId(Department department,
                                                               Level level,
                                                               Long semesterId
    );
    boolean existsByDepartmentAndLevelAndSemester(Department department,
                                                      Level level,
                                                      Semester semester
    );
}
