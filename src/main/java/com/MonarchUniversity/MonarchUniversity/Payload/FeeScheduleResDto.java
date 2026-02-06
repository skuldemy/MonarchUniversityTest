package com.MonarchUniversity.MonarchUniversity.Payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeeScheduleResDto {
    private Long feeScheduleId;
    private String levelName;
    private Long levelId;
    private String departmentName;
    private Long departmentId;
    private BigDecimal totalAmount;
    private List<FeeItemResDto> fees;
}

