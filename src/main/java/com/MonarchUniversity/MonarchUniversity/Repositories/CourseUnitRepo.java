package com.MonarchUniversity.MonarchUniversity.Repositories;

import com.MonarchUniversity.MonarchUniversity.Model.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseUnitRepo extends JpaRepository<CourseUnit, Long> {
    CourseUnit getCourseUnitByDepartmentAndLevelAndSemesterName(Department department,
                                                               Level level,
                                                               String semester
    );
    boolean existsByDepartmentAndLevelAndSemesterName(Department department,
                                                      Level level,
                                                      String semester
    );
}
