package com.MonarchUniversity.MonarchUniversity.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(
            fetch = FetchType.LAZY
    )
    @JoinColumn(name = "department_id")
    private Department department;
    @ManyToOne(
            fetch = FetchType.LAZY
    )
    @JoinColumn(name = "level_id")
    private Level level;
    private String courseTitle;
    private String courseType; // CORE, ELECTIVE, REQUIRED
    private String courseCode;
    private Integer courseUnit; // 1,2,3,4
}
