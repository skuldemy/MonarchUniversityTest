package com.MonarchUniversity.MonarchUniversity.Payload;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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
    @Pattern(regexp = "^[0-9]+$", message = "Level number must be numeric")
    private String levelNumber;

    private Integer capacity;
}
