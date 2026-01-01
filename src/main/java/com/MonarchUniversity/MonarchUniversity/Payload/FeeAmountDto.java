package com.MonarchUniversity.MonarchUniversity.Payload;

import lombok.Data;

import java.math.BigDecimal;

@Data
public final class FeeAmountDto {
    private Long feeTypeId;
    private BigDecimal amount;
}