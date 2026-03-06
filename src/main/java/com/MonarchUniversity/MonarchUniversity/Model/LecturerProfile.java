package com.MonarchUniversity.MonarchUniversity.Model;

import java.util.List;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LecturerProfile {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String fullName;
	@OneToOne
	@JoinColumn(name="user_id")
	private User user;
    @ManyToMany
    @JoinTable(
            name = "lecturer_courses",
            joinColumns = @JoinColumn(name = "lecturer_id"),
            inverseJoinColumns = @JoinColumn(name = "course_id")
    )
    private List<Course> courses;

    @ManyToOne(
            fetch = FetchType.LAZY
    )
    @JoinColumn(
            name = "department_id"
    )
    private Department department;
    @ManyToOne(
            fetch = FetchType.LAZY
    )
    @JoinColumn(
            name = "level_id"
    )
    private Level level;

    @Enumerated(EnumType.STRING)
    private LecturerType lecturerType;

    public enum LecturerType{
        HOD, DEAN, LEVEL_ADVISER
    }
}
