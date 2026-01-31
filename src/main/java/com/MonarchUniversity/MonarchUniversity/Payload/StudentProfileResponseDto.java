package com.MonarchUniversity.MonarchUniversity.Payload;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentProfileResponseDto {
	private Long id;
	private String firstName;
	private String middleName;
	private String lastName;
	private String gender;
	private LocalDate dateOfBirth;
	private String nationality;
	private String stateOfOrigin;
	private String lga;
	private String facultyName;
	private String departmentName;
	private String levelName;
	private LocalDate admissionYear;
	private String matricNumber;
	private String modeOfEntry;
	private String emailAddress;
	private String phoneNumber;
	private String homeAddress;
	private String status;
}
