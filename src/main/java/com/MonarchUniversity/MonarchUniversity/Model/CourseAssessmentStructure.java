package com.MonarchUniversity.MonarchUniversity.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class CourseAssessmentStructure {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer maxCa;

    private Integer maxExam;

    private Integer total;

    @OneToOne
    @JoinColumn(name = "semester_course_id")
    private SemesterCourse semesterCourse;
}
