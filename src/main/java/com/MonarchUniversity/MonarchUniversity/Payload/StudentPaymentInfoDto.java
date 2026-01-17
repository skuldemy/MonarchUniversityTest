package com.MonarchUniversity.MonarchUniversity.Payload;

import com.MonarchUniversity.MonarchUniversity.Model.StudentPayment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentPaymentInfoDto {
    private Long studentId;
    private String fullName;
    private String matricNumber;
    private BigDecimal amountPaid;
    private BigDecimal totalFee;
    private BigDecimal remainingAmount;
    private StudentPayment.ApprovalStatus feeStatus;
    private StudentPayment.PaymentStatus paymentStatus;
    private List<FeeTypeStatus> feeTypeStatusList;

}

