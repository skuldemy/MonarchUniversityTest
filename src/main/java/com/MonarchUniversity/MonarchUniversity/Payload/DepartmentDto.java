package com.MonarchUniversity.MonarchUniversity.Payload;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentDto {
    private Long id;
    @NotBlank(message = "Department name is required")
    private String departmentName;
    @NotBlank(message = "Department code is required")
    private String departmentCode;
    private Long facultyId;
    private String facultyName;
    private String departmentDescription;
    private String officeLocation;
    private Integer establishedYear;

    public DepartmentDto(Long id, @NotBlank(message = "Department name is required") String departmentName) {
		this.id = id;
		this.departmentName = departmentName;
	}
	
    
    
}
