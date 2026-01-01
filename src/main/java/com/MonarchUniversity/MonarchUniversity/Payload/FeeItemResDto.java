package com.MonarchUniversity.MonarchUniversity.Payload;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class FeeItemResDto {
    private Long feeTypeId;
    private String feeTypeName;
    private BigDecimal amount;
}
