package com.MonarchUniversity.MonarchUniversity.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Material {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(
            fetch = FetchType.LAZY
    )
    @JoinColumn(
            name = "semester_course_id"
    )
    private SemesterCourse semesterCourse;
    private String materialType;
    private String week;
    private String fileUrl;

    @ManyToOne(
            fetch = FetchType.LAZY
    )
    @JoinColumn(
            name = "lecturer_id"
    )
    private LecturerProfile lecturerProfile;

}

