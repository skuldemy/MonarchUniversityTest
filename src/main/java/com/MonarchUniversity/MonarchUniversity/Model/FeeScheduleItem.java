package com.MonarchUniversity.MonarchUniversity.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeeScheduleItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "feeSchedule_id")
    private FeeSchedule feeSchedule;

    private Integer priority;


    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name="feeType_id")
    private FeeType feeType;

    private BigDecimal amount;
    @Column(nullable = false)
    private BigDecimal amountPaid = BigDecimal.ZERO;

    private BigDecimal remainingAmount;
}
