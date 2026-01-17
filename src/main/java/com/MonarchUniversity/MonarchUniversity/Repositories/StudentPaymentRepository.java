package com.MonarchUniversity.MonarchUniversity.Repositories;

import com.MonarchUniversity.MonarchUniversity.Model.FeeSchedule;
import com.MonarchUniversity.MonarchUniversity.Model.StudentPayment;
import com.MonarchUniversity.MonarchUniversity.Model.StudentProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudentPaymentRepository extends JpaRepository<StudentPayment, Long> {

    Optional<StudentPayment> findByStudentAndFeeSchedule(
            StudentProfile student,
            FeeSchedule feeSchedule
    );

    Optional<StudentPayment> findByStudentId(Long id);
}
