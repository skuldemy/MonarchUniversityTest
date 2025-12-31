package com.MonarchUniversity.MonarchUniversity.Payload;

import com.MonarchUniversity.MonarchUniversity.Entity.Session;
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
    @NotNull(message = "registration start date cannot be empty")
    private LocalDate registrationStartDate;
    @NotNull(message = "registration end date cannot be empty")
    private LocalDate registrationEndDate;
    private String semesterName;
}
