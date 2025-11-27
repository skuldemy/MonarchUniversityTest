package com.MonarchUniversity.MonarchUniversity.Payload;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FacultyDto {
    private Long id;
    @NotBlank(message = "Faculty name is required")
    private String facultyName;
    @NotBlank(message = "Faculty code is required")
    private String facultyCode;
    private String facultyDescription;
    private String facultyEmail;
    private String facultyAddress;
    private Integer establishedYear;
    private String facultyMotto;

    public FacultyDto(Long id, @NotBlank(message = "Faculty name is required") String facultyName) {
		this.id = id;
		this.facultyName = facultyName;
	}
    
    
}
