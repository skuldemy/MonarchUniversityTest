package com.MonarchUniversity.MonarchUniversity.Payload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProgramDto {
    private Long id;

    @NotBlank(message = "Program name is required")
    private String programName;

    @NotBlank(message = "Program code is required")
    private String programCode;

    @NotNull(message = "Department ID is required")
    private Long departmentId;

    @NotBlank(message = "Duration is required")
    private String duration;

    @NotBlank(message = "Program type is required")
    private String programType;

    @NotBlank(message = "Mode of study is required")
    private String modeOfStudy;

    private String entryRequirements; 
}
