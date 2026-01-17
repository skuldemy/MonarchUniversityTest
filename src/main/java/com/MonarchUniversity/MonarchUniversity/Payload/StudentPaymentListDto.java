package com.MonarchUniversity.MonarchUniversity.Payload;

import com.MonarchUniversity.MonarchUniversity.Model.StudentPayment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentPaymentListDto {
    private Long id;
    private String fullName;
    private String matricNumber;
    private BigDecimal amountPaid;
    private BigDecimal totalFee;
    private BigDecimal remainingAmount;
    private StudentPayment.ApprovalStatus feeStatus;
    private StudentPayment.PaymentStatus paymentStatus;
    private Integer scholarshipPercentage;
}
