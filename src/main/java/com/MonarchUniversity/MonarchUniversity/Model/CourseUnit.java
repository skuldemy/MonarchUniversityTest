package com.MonarchUniversity.MonarchUniversity.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CourseUnit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "department_id"
    )
    private Department department;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "level_id"
    )
    private Level level;
    private String semesterName;
    private Integer minUnits;
    private Integer maxUnits;
}
