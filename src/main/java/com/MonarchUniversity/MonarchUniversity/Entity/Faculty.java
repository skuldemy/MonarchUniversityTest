package com.MonarchUniversity.MonarchUniversity.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Faculty {
	@Id
	@GeneratedValue
	private Long id;
	private String facultyName;
	private String facultyCode;
	private String facultyDescription;
	private String facultyEmail;
	private String facultyAddress;
	private Integer establishedYear;
	private String facultyMotto;
}
