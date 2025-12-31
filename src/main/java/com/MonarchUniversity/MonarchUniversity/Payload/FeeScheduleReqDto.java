package com.MonarchUniversity.MonarchUniversity.Payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeeScheduleReqDto {
    private Long levelId;
    private Long departmentId;
    private List<FeeAmountDto> feeTypeDtoList;


}

@Data
class FeeAmountDto {
    private Long feeTypeId;
    private BigDecimal amount;
}

