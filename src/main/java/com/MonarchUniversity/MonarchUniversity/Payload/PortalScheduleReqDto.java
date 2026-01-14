package com.MonarchUniversity.MonarchUniversity.Payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PortalScheduleReqDto {
    private Long feeTypeId;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean status;
}
