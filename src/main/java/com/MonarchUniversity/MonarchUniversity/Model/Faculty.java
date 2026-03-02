package com.MonarchUniversity.MonarchUniversity.Model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Faculty {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String facultyName;
	private String facultyCode;
	private String facultyDescription;
	private String facultyEmail;
	private String facultyAddress;
	private Integer establishedYear;
	private String facultyMotto;
    @OneToMany(mappedBy = "faculty")
    private List<Department> departments;
	
}
