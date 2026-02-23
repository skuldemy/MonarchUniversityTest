package com.MonarchUniversity.MonarchUniversity.Model;

import jakarta.persistence.*;

@Entity
public class CourseRegistration {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(
            fetch = FetchType.LAZY
    )
    @JoinColumn(
            name = "studentProfile_id"
    )
    private StudentProfile studentProfile;
    @ManyToOne(
            fetch = FetchType.LAZY
    )
    @JoinColumn(
            name = "semesterCourse_id"
    )
    private SemesterCourse semesterCourse;
}
