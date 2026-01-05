package com.MonarchUniversity.MonarchUniversity.Service;

import com.MonarchUniversity.MonarchUniversity.Entity.*;
import com.MonarchUniversity.MonarchUniversity.Exception.ResponseNotFoundException;
import com.MonarchUniversity.MonarchUniversity.Payload.StudentPaymentListDto;
import com.MonarchUniversity.MonarchUniversity.Payload.StudentProfileRequestDto;
import com.MonarchUniversity.MonarchUniversity.Repositories.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class StudentPaymentService {
    private final LevelRepository levelRepository;
    private final ProgramRepository programRepository;
    private final FeeScheduleRepo feeScheduleRepo;
    private final FeeTypeRepo feeTypeRepo;
    private final FeeScheduleItemRepo feeScheduleItemRepo;
    private final StudentProfileRepo studentProfileRepo;
    private final StudentPaymentRepository studentPaymentRepo;

    private BigDecimal calculateTotal(List<FeeScheduleItem> items) {
        return items.stream()
                .map(FeeScheduleItem::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }


    public List<StudentPaymentListDto> getALlStudentsPayment(Long levelId, Long programId) {

        Level level = levelRepository.findById(levelId)
                .orElseThrow(() -> new ResponseNotFoundException("No such level"));

        Program program = programRepository.findById(programId)
                .orElseThrow(() -> new ResponseNotFoundException("No such program"));

        FeeSchedule feeSchedule = feeScheduleRepo
                .findByLevelAndProgram(level, program)
                .orElseThrow(() -> new ResponseNotFoundException(
                        "No fee schedule for this level and program"));

        List<FeeScheduleItem> items =
                feeScheduleItemRepo.findByFeeSchedule(feeSchedule);

        BigDecimal totalFee = calculateTotal(items);

        List<StudentProfile> students =
                studentProfileRepo.findByProgramAndLevel(program, level);

        List<StudentPaymentListDto> response = new ArrayList<>();

        for (StudentProfile student : students) {

            StudentPayment payment = studentPaymentRepo
                    .findByStudentAndFeeSchedule(student, feeSchedule)
                    .orElse(null);

            if (payment == null) {

                response.add(new StudentPaymentListDto(
                        student.getId(),
                        student.getFirstName() + " " + student.getLastName(),
                        student.getMatricNumber(),
                        BigDecimal.ZERO,
                        totalFee,
                        StudentPayment.ApprovalStatus.PENDING,
                        StudentPayment.PaymentStatus.NO_PAYMENT,
                        0
                ));
            } else {

                response.add(mapToDto(payment));
            }
        }

        return response;
    }


    @Transactional
    public StudentPaymentListDto applyScholarship(
            Long studentId,
            Integer scholarshipPercentage
    ) {

        if (scholarshipPercentage < 0 || scholarshipPercentage > 100) {
            throw new IllegalArgumentException("Scholarship percentage must be between 0 and 100");
        }

        StudentProfile student = studentProfileRepo.findById(studentId)
                .orElseThrow(() -> new ResponseNotFoundException("Student not found"));

        FeeSchedule feeSchedule = feeScheduleRepo
                .findByLevelAndProgram(student.getLevel(), student.getProgram())
                .orElseThrow(() -> new ResponseNotFoundException("Fee schedule not found"));

        StudentPayment payment = studentPaymentRepo
                .findByStudentAndFeeSchedule(student, feeSchedule)
                .orElseGet(() -> {

                    BigDecimal baseFee = calculateTotal(
                            feeScheduleItemRepo.findByFeeSchedule(feeSchedule)
                    );

                    StudentPayment p = new StudentPayment();
                    p.setStudent(student);
                    p.setFeeSchedule(feeSchedule);
                    p.setTotalFee(baseFee);
                    p.setScholarshipPercentage(0);
                    p.setAmountPaid(BigDecimal.ZERO);
                    p.setPaymentStatus(StudentPayment.PaymentStatus.PENDING);
                    p.setApprovalStatus(StudentPayment.ApprovalStatus.PENDING);

                    return studentPaymentRepo.save(p);
                });

        BigDecimal discount = payment.getTotalFee()
                .multiply(BigDecimal.valueOf(scholarshipPercentage))
                .divide(BigDecimal.valueOf(100));

        BigDecimal newTotalFee = payment.getTotalFee().subtract(discount);

        payment.setScholarshipPercentage(scholarshipPercentage);
        payment.setTotalFee(newTotalFee);

        studentPaymentRepo.save(payment);

        return mapToDto(payment);
    }

    private StudentPaymentListDto mapToDto(StudentPayment payment) {
        return new StudentPaymentListDto(
                payment.getStudent().getId(),
                payment.getStudent().getFirstName() + " " + payment.getStudent().getLastName(),
                payment.getStudent().getMatricNumber(),
                payment.getAmountPaid(),
                payment.getTotalFee(),
                payment.getApprovalStatus(),
                payment.getPaymentStatus(),
                payment.getScholarshipPercentage()
        );
    }

}
