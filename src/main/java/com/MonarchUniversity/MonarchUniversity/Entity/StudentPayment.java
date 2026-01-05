package com.MonarchUniversity.MonarchUniversity.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "student_id"
    )

    private StudentProfile student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "feeSchedule_id"
    )
    private FeeSchedule feeSchedule;

    private BigDecimal totalFee;
    private Integer scholarshipPercentage;
    private BigDecimal amountPaid;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;  // PENDING, PARTIAL, PAID

    @Enumerated(EnumType.STRING)
    private ApprovalStatus approvalStatus; // PENDING, APPROVED, REJECTED

    public enum PaymentStatus {
        PENDING,
        PARTIAL,
        NO_PAYMENT,
        PAID
    }

    public enum ApprovalStatus {
        PENDING,
        APPROVED,
        REJECTED
    }

}

