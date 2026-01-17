package com.MonarchUniversity.MonarchUniversity.Model;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
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

	//	@ManyToOne
//	@JoinColumn(name="faculty_id")
//	private Faculty faculty;
//	@ManyToOne
//	@JoinColumn(name="department_id")
//	private Department department;
	
	@ManyToOne
	@JoinColumn(name="role_id")
	private Role role;
	@ManyToMany
	@JoinTable(
	    name = "lecturer_programs",
	    joinColumns = @JoinColumn(name = "lecturer_id"),
	    inverseJoinColumns = @JoinColumn(name = "program_id")
	)
	private List<Program> courses;
	
	
}
