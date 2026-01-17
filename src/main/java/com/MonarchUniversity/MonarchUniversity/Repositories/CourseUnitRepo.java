package com.MonarchUniversity.MonarchUniversity.Repositories;

import com.MonarchUniversity.MonarchUniversity.Model.CourseUnit;
import com.MonarchUniversity.MonarchUniversity.Model.Level;
import com.MonarchUniversity.MonarchUniversity.Model.Program;
import com.MonarchUniversity.MonarchUniversity.Model.Semester;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseUnitRepo extends JpaRepository<CourseUnit, Long> {
    List<CourseUnit> getCourseUnitByProgramAndLevelAndSemesterName(Program program,
                                                               Level level,
                                                               String semester
                                                               );
}
