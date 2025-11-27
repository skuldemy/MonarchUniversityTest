package com.MonarchUniversity.MonarchUniversity.Payload;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LecturerRequestDto {
	 @NotBlank(message = "Full name is required")
	    private String fullName;

	    @NotBlank(message = "Email address is required")
	    @Email(message = "Email address is invalid")
	    private String emailAddress;

	    @NotBlank(message = "Password is required")
	    @Size(min = 6, message = "Password must be at least 6 characters")
	    private String password;
	private Long facultyId;
	private Long departmentId;
	private Long roleId;
	private List<Long> courses = new ArrayList<>();
}
