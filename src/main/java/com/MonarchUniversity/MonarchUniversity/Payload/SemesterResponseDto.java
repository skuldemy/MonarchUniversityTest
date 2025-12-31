package com.MonarchUniversity.MonarchUniversity.Payload;

import com.MonarchUniversity.MonarchUniversity.Entity.Session;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SemesterResponseDto {
    private Long id;
    private String session;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate registrationStartDate;
    private LocalDate registrationEndDate;
    private String semesterName;
}
