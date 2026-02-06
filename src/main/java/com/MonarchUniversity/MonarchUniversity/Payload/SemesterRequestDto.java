package com.MonarchUniversity.MonarchUniversity.Payload;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SemesterRequestDto {
    @NotNull(message = "session is required")
    private Long sessionId;
    @NotNull(message = "start date cannot be empty")
    private LocalDate startDate;
    @NotNull(message = "end date cannot be empty")
    private LocalDate endDate;
    private String semesterName;
}
