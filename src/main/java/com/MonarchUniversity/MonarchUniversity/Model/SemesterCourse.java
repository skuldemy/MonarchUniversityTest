package com.MonarchUniversity.MonarchUniversity.Model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
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
