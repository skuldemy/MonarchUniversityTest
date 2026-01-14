package com.MonarchUniversity.MonarchUniversity.Payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequestDto {
    private String paymentType; //Half or FUll
    private BigDecimal customType;
}
