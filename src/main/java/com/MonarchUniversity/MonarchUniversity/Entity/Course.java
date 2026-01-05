package com.MonarchUniversity.MonarchUniversity.Entity;

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
    @JoinColumn(name = "program_id")
    private Program program;
    @ManyToOne(
            fetch = FetchType.LAZY
    )
    @JoinColumn(name = "level_id")
    private Level level;
    private String courseTitle;
    private String courseType; // elective, core, specialization
    private String courseCode;
    private Integer courseUnit; // 1,2,3,4
}
