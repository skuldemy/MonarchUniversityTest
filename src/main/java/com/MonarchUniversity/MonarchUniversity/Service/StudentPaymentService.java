package com.MonarchUniversity.MonarchUniversity.Service;

import com.MonarchUniversity.MonarchUniversity.Entity.*;
import com.MonarchUniversity.MonarchUniversity.Exception.ResponseNotFoundException;
import com.MonarchUniversity.MonarchUniversity.Payload.StudentPaymentListDto;
import com.MonarchUniversity.MonarchUniversity.Payload.StudentProfileRequestDto;
import com.MonarchUniversity.MonarchUniversity.Repositories.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

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

    private BigDecimal calculateTotal(List<FeeScheduleItem> items) {
        return items.stream()
                .map(FeeScheduleItem::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }


    public List<StudentPaymentListDto> getALlStudentsPayment(Long levelId, Long programId){

        Level level = levelRepository.findById(levelId).orElseThrow(()-> new ResponseNotFoundException("No such level"));
        Program program = programRepository.findById(programId).orElseThrow(()-> new ResponseNotFoundException("No such program"));
        FeeSchedule feeSchedule = feeScheduleRepo.findByLevelAndProgram(level,program).orElseThrow(()-> new ResponseNotFoundException("No fee schedule for this level and program, consider setting one"));

        List<FeeScheduleItem> items =
                feeScheduleItemRepo.findByFeeSchedule(feeSchedule);
        List<StudentProfile> studentProfiles = studentProfileRepo.findByProgramAndLevel(program, level);
        List<StudentPaymentListDto> paymentListDtos = new ArrayList<>();

        BigDecimal totalFee = calculateTotal(items);

        for (StudentProfile studentProfile : studentProfiles){
            StudentPaymentListDto dto = new StudentPaymentListDto();
            dto.setId(studentProfile.getId());
            dto.setFullName(studentProfile.getFirstName() + " " + studentProfile.getLastName());
            dto.setMatricNumber(studentProfile.getMatricNumber());
            dto.setAmountPaid(BigDecimal.ZERO);
            dto.setTotalFee(totalFee);
            dto.setPaymentStatus("Pending");
            dto.setFeeStatus("No_Payment");
            dto.setScholarshipPercentage(0);

            paymentListDtos.add(dto);
        }



                return paymentListDtos;
    }
}
