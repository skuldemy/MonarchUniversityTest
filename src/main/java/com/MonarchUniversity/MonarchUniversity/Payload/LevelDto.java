package com.MonarchUniversity.MonarchUniversity.Payload;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LevelDto {

    private Long id;

    @NotNull(message = "Department is required")
    private Long departmentId;

    private String programName;
    
    @NotBlank(message = "Level number is required")
    private String levelNumber;

    @NotBlank(message = "Semester is required")
    private String semester;

    private Integer capacity;
}
