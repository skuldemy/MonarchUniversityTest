package com.MonarchUniversity.MonarchUniversity.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SemesterCourse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(
            fetch = FetchType.LAZY
    )
    @JoinColumn(
            name = "course_id"
    )
    private Course course;
    @ManyToOne(
            fetch = FetchType.LAZY
    )
    @JoinColumn(
            name = "semester_id"
    )
    private Semester semester;

}
