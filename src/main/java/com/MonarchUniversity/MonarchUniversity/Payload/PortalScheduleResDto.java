package com.MonarchUniversity.MonarchUniversity.Payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PortalScheduleResDto {
    private Long id;
    private String feeType;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
}
