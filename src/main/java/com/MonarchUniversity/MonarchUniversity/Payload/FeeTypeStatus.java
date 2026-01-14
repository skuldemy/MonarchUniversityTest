package com.MonarchUniversity.MonarchUniversity.Payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeeTypeStatus{
    private Long feeTypeId;
    private String feeTypeName;
    private BigDecimal amount;
    private BigDecimal amountPaid;
    private BigDecimal remainingAmount;

}

