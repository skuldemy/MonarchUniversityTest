package com.MonarchUniversity.MonarchUniversity.Payload;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LecturerRequestDto {
	private String fullName;
	private String emailAddress;
	private String password;
	private Long facultyId;
	private Long departmentId;
	private Long roleId;
	private List<Long> courses = new ArrayList<>();
}
