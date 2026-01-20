package com.MonarchUniversity.MonarchUniversity.Impl;

import com.MonarchUniversity.MonarchUniversity.Model.*;
import com.MonarchUniversity.MonarchUniversity.Exception.ResponseNotFoundException;
import com.MonarchUniversity.MonarchUniversity.Payload.*;
import com.MonarchUniversity.MonarchUniversity.Repositories.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;



@Service
@AllArgsConstructor
@Slf4j
public class StudentPaymentService {
    private final LevelRepository levelRepository;
    private final ProgramRepository programRepository;
    private final FeeScheduleRepo feeScheduleRepo;
    private final FeeTypeRepo feeTypeRepo;
    private final FeeScheduleItemRepo feeScheduleItemRepo;
    private final StudentProfileRepo studentProfileRepo;
    private final StudentPaymentRepository studentPaymentRepo;
    private final UserRepository userRepository;


    private StudentProfile getLoggedInStudentProfile() {
        org.springframework.security.core.userdetails.User springUser =
                (org.springframework.security.core.userdetails.User) SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getPrincipal();

        User userEntity = userRepository.findByUsername(springUser.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return studentProfileRepo.findByUser(userEntity)
                .orElseThrow(() -> new RuntimeException("StudentProfile not found"));
    }


    public LevelProgramDto getLevelAndProgram(){
        StudentProfile studentProfile = getLoggedInStudentProfile();



        Level level = studentProfile.getLevel();
        Program program = studentProfile.getProgram();

        FeeSchedule feeSchedule = feeScheduleRepo
                .findByLevelAndProgram(level, program)
                .orElseThrow(() -> new ResponseNotFoundException("Fee schedule not found"));

        List<FeeScheduleItem> items = feeScheduleItemRepo.findByFeeScheduleOrderByPriorityAsc(feeSchedule);

        BigDecimal baseFee = calculateTotal(items);

        if (baseFee == null) {
            baseFee = BigDecimal.ZERO;
        }

        return new LevelProgramDto(level.getLevelNumber(),program.getProgramName(), baseFee);
    }


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

                BigDecimal remainingAmount = totalFee; // no amount paid yet

                response.add(new StudentPaymentListDto(
                        student.getId(),
                        student.getFirstName() + " " + student.getLastName(),
                        student.getMatricNumber(),
                        BigDecimal.ZERO,            // amountPaid
                        totalFee,                   // totalFee
                        remainingAmount,            // balance
                        StudentPayment.ApprovalStatus.PENDING,
                        StudentPayment.PaymentStatus.NO_PAYMENT,
                        0
                ));
            } else {

                BigDecimal remainingAmount = totalFee.subtract(payment.getAmountPaid());

                StudentPaymentListDto dto = mapToDto(payment);
                dto.setRemainingAmount(remainingAmount);

                response.add(dto);
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

        BigDecimal remainingAmount = payment.getTotalFee()
                .subtract(payment.getAmountPaid());

        return new StudentPaymentListDto(
                payment.getStudent().getId(),
                payment.getStudent().getFirstName() + " " + payment.getStudent().getLastName(),
                payment.getStudent().getMatricNumber(),
                payment.getAmountPaid(),
                payment.getTotalFee(),
                remainingAmount,
                payment.getApprovalStatus(),
                payment.getPaymentStatus(),
                payment.getScholarshipPercentage()
        );
    }

    public StudentPaymentInfoDto getStudentPaymentList() {

        StudentProfile studentProfile = getLoggedInStudentProfile();

        FeeSchedule feeSchedule = feeScheduleRepo
                .findByLevelAndProgram(studentProfile.getLevel(), studentProfile.getProgram())
                .orElseThrow(() -> new ResponseNotFoundException("No fee schedule found"));

        List<FeeScheduleItem> items = feeScheduleItemRepo
                .findByFeeScheduleOrderByPriorityAsc(feeSchedule);

        StudentPayment payment = studentPaymentRepo
                .findByStudentAndFeeSchedule(studentProfile, feeSchedule)
                .orElse(null);

        StudentPaymentInfoDto dto = new StudentPaymentInfoDto();
        dto.setStudentId(studentProfile.getId());
        dto.setFullName(studentProfile.getLastName() + " " + studentProfile.getFirstName());
        dto.setMatricNumber(studentProfile.getMatricNumber());

        BigDecimal totalFee = calculateTotal(items);
        dto.setTotalFee(totalFee);

        // 💡 If student has not paid anything
        if (payment == null) {
            dto.setAmountPaid(BigDecimal.ZERO);
            dto.setRemainingAmount(totalFee);
            dto.setPaymentStatus(StudentPayment.PaymentStatus.NO_PAYMENT);

            List<FeeTypeStatus> emptyList = new ArrayList<>();
            for (FeeScheduleItem item : items) {
                FeeTypeStatus status = new FeeTypeStatus();
                status.setFeeTypeId(item.getFeeType().getId());
                status.setFeeTypeName(item.getFeeType().getName());
                status.setAmount(item.getAmount());
                status.setAmountPaid(BigDecimal.ZERO);
                status.setRemainingAmount(item.getAmount());
                emptyList.add(status);
            }
            dto.setFeeTypeStatusList(emptyList);

            return dto;
        }

        // 💡 Student HAS paid something
        dto.setAmountPaid(payment.getAmountPaid());
        dto.setRemainingAmount(payment.getTotalFee().subtract(payment.getAmountPaid()));
        dto.setPaymentStatus(payment.getPaymentStatus());

        List<FeeTypeStatus> feeTypeStatusList = new ArrayList<>();

        BigDecimal totalPaid = payment.getAmountPaid();

        // 💡 Compute proportional amounts (same as makePayment)


        for (FeeScheduleItem item : items) {

            BigDecimal proportionalPaid = BigDecimal.ZERO;

            if (totalPaid.compareTo(BigDecimal.ZERO) > 0) {
                proportionalPaid = item.getAmount()
                        .multiply(totalPaid)
                        .divide(totalFee, 2, BigDecimal.ROUND_HALF_UP);
            }

            BigDecimal itemRemaining = item.getAmount().subtract(proportionalPaid);

            FeeTypeStatus status = new FeeTypeStatus();
            status.setFeeTypeId(item.getFeeType().getId());
            status.setFeeTypeName(item.getFeeType().getName());
            status.setAmount(item.getAmount());
            status.setAmountPaid(proportionalPaid);
            status.setRemainingAmount(itemRemaining);

            feeTypeStatusList.add(status);
        }

        dto.setFeeTypeStatusList(feeTypeStatusList);

        return dto;
    }



    @Transactional
    public StudentPaymentInfoDto makePayment(PaymentRequestDto dto) {


        StudentProfile student = getLoggedInStudentProfile();

            FeeSchedule feeSchedule = feeScheduleRepo
                .findByLevelAndProgram(student.getLevel(), student.getProgram())
                .orElseThrow(() -> new ResponseNotFoundException("Fee schedule not found"));


        List<FeeScheduleItem> items = feeScheduleItemRepo.findByFeeScheduleOrderByPriorityAsc(feeSchedule);


        StudentPayment payment = studentPaymentRepo
                .findByStudentAndFeeSchedule(student, feeSchedule)
                .orElseGet(() -> {
                    BigDecimal baseFee = calculateTotal(items);
                    StudentPayment p = new StudentPayment();
                    p.setStudent(student);
                    p.setFeeSchedule(feeSchedule);
                    p.setTotalFee(baseFee);
                    p.setAmountPaid(BigDecimal.ZERO);
                    p.setPaymentStatus(StudentPayment.PaymentStatus.PENDING);
                    return studentPaymentRepo.save(p);
                });


        if (payment.getTotalFee() == null) payment.setTotalFee(BigDecimal.ZERO);
        if (payment.getAmountPaid() == null) payment.setAmountPaid(BigDecimal.ZERO);


        if (payment.getTotalFee().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseNotFoundException(
                    "Total fee is zero. Fee schedule is not properly configured."
            );
        }
        BigDecimal amountToPay;


        switch (dto.getPaymentType()) {
            case "FULL":
                amountToPay = payment.getTotalFee().subtract(payment.getAmountPaid());
                break;
            case "HALF":
                amountToPay = payment.getTotalFee()
                        .subtract(payment.getAmountPaid())
                        .divide(BigDecimal.valueOf(2));
                break;
            case "CUSTOM":
                amountToPay = dto.getCustomType() != null ? dto.getCustomType() : BigDecimal.ZERO;
                break;
            default:
                throw new IllegalArgumentException("Invalid payment type");
        }


        BigDecimal remainingBalance = payment.getTotalFee().subtract(payment.getAmountPaid());
        if (amountToPay.compareTo(remainingBalance) > 0) {
            throw new IllegalArgumentException("Cannot pay more than remaining balance");
        }


        splitPaymentAcrossItemsMemory(items, payment, amountToPay);


        return getStudentPaymentListMemory(items, payment);
    }

    /**
     * Split payment across items proportionally in memory (template items remain unchanged)
     */
    private void splitPaymentAcrossItemsMemory(List<FeeScheduleItem> items,
                                               StudentPayment payment,
                                               BigDecimal amountToPay) {

        BigDecimal remaining = amountToPay;

        // Sort items by priority (optional, keeps logic consistent)
        items.sort((a, b) -> {
            String nameA = a.getFeeType().getName();
            String nameB = b.getFeeType().getName();

            if ("Course Registration".equalsIgnoreCase(nameA)) return -1;
            if ("Course Registration".equalsIgnoreCase(nameB)) return 1;
            if ("Tuition".equalsIgnoreCase(nameA)) return -1;
            if ("Tuition".equalsIgnoreCase(nameB)) return 1;

            int p1 = a.getPriority() != null ? a.getPriority() : Integer.MAX_VALUE;
            int p2 = b.getPriority() != null ? b.getPriority() : Integer.MAX_VALUE;
            return Integer.compare(p1, p2);
        });

        // Calculate total remaining
        BigDecimal totalRemaining = payment.getTotalFee().subtract(payment.getAmountPaid());

        if (totalRemaining.compareTo(BigDecimal.ZERO) <= 0) {
            return; // nothing to pay
        }

        // Ensure we don't overpay
        BigDecimal toAllocate = remaining.min(totalRemaining);

        // Update student's total paid
        payment.setAmountPaid(payment.getAmountPaid().add(toAllocate));
        payment.setApprovalStatus(StudentPayment.ApprovalStatus.PENDING);

        // Update payment status
        if (payment.getAmountPaid().compareTo(payment.getTotalFee()) >= 0) {
            payment.setPaymentStatus(StudentPayment.PaymentStatus.PAID);
        } else {
            payment.setPaymentStatus(StudentPayment.PaymentStatus.PARTIAL);
        }

        studentPaymentRepo.save(payment);
    }

//    private void splitPaymentAcrossItemsMemory(List<FeeScheduleItem> items,
//                                               StudentPayment payment,
//                                               BigDecimal amountToPay) {
//
//        BigDecimal remaining = amountToPay;
//
//        // Sort by priority: Course Registration first, Tuition second, then others
//        items.sort((a, b) -> {
//            String nameA = a.getFeeType().getName();
//            String nameB = b.getFeeType().getName();
//
//            if ("Course Registration".equalsIgnoreCase(nameA)) return -1;
//            if ("Course Registration".equalsIgnoreCase(nameB)) return 1;
//            if ("Tuition".equalsIgnoreCase(nameA)) return -1;
//            if ("Tuition".equalsIgnoreCase(nameB)) return 1;
//
//            int p1 = a.getPriority() != null ? a.getPriority() : Integer.MAX_VALUE;
//            int p2 = b.getPriority() != null ? b.getPriority() : Integer.MAX_VALUE;
//            return Integer.compare(p1, p2);
//        });
//
//        for (FeeScheduleItem item : items) {
//            BigDecimal itemPaid = item.getAmountPaid() != null ? item.getAmountPaid() : BigDecimal.ZERO;
//            BigDecimal itemRemaining = item.getAmount().subtract(itemPaid);
//
//            if (itemRemaining.compareTo(BigDecimal.ZERO) <= 0) continue; // already paid
//
//            BigDecimal payNow;
//
//            if ("Course Registration".equalsIgnoreCase(item.getFeeType().getName())) {
//                // Always pay full course registration first
//                payNow = itemRemaining.min(remaining);
//            } else {
//                // For other items, pay whatever is left
//                payNow = remaining.min(itemRemaining);
//            }
//
//            item.setAmountPaid(itemPaid.add(payNow));
//            remaining = remaining.subtract(payNow);
//
//            if (remaining.compareTo(BigDecimal.ZERO) <= 0) break; // no more money left
//        }
//
//        // Update total payment
//        BigDecimal totalPaidThisRound = amountToPay.subtract(remaining);
//        payment.setAmountPaid(payment.getAmountPaid().add(totalPaidThisRound));
//        payment.setApprovalStatus(StudentPayment.ApprovalStatus.PENDING);
//
//        // Update payment status
//        if (payment.getAmountPaid().compareTo(payment.getTotalFee()) >= 0) {
//            payment.setPaymentStatus(StudentPayment.PaymentStatus.PAID);
//        } else {
//            payment.setPaymentStatus(StudentPayment.PaymentStatus.PARTIAL);
//        }
//
//        studentPaymentRepo.save(payment);
//    }


    private StudentPaymentInfoDto getStudentPaymentListMemory(List<FeeScheduleItem> items,
                                                              StudentPayment payment) {

        StudentProfile studentProfile = payment.getStudent();

        StudentPaymentInfoDto dto = new StudentPaymentInfoDto();
        dto.setStudentId(studentProfile.getId());
        dto.setFullName(studentProfile.getLastName() + " " + studentProfile.getFirstName());
        dto.setMatricNumber(studentProfile.getMatricNumber());
        dto.setTotalFee(payment.getTotalFee());
        dto.setAmountPaid(payment.getAmountPaid());
        dto.setRemainingAmount(payment.getTotalFee().subtract(payment.getAmountPaid()));
        dto.setPaymentStatus(payment.getPaymentStatus());

        List<FeeTypeStatus> feeTypeStatusList = new ArrayList<>();

        BigDecimal totalFee = payment.getTotalFee();
        BigDecimal totalPaid = payment.getAmountPaid();

        for (FeeScheduleItem item : items) {
            // Calculate proportional paid for this item
            BigDecimal itemPaid = item.getAmount()
                    .multiply(totalPaid)
                    .divide(totalFee, 2, BigDecimal.ROUND_HALF_UP);

            FeeTypeStatus status = new FeeTypeStatus();
            status.setFeeTypeId(item.getFeeType().getId());
            status.setFeeTypeName(item.getFeeType().getName());
            status.setAmount(item.getAmount());
            status.setAmountPaid(itemPaid);
            status.setRemainingAmount(item.getAmount().subtract(itemPaid));

            feeTypeStatusList.add(status);
        }

        dto.setFeeTypeStatusList(feeTypeStatusList);
        return dto;
    }

}
