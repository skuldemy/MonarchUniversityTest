package com.MonarchUniversity.MonarchUniversity.Repositories;

import com.MonarchUniversity.MonarchUniversity.Model.CourseRegistration;
import com.MonarchUniversity.MonarchUniversity.Model.Semester;
import com.MonarchUniversity.MonarchUniversity.Model.StudentProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CourseRegistrationRepo extends JpaRepository<CourseRegistration, Long> {

    List<CourseRegistration>
    findByStudentProfileAndSemesterCourse_Semester(
            StudentProfile studentProfile,
            Semester semester
    );
    List<CourseRegistration> findBySemesterCourse_Id(Long semesterCourseId);
}
