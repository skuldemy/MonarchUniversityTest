package com.MonarchUniversity.MonarchUniversity.Payload;

import java.util.List;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LecturerResponseDto {
	private Long id;
	private String fullName;
	private String emailAddress;
	private String onBoard; // online or offline
	private Set<String> roleName;
	private String departmentName;
	private List<String> coursesOffering;
	private String status;
	
}
