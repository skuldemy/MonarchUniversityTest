package com.MonarchUniversity.MonarchUniversity.Service;

import com.MonarchUniversity.MonarchUniversity.Entity.*;
import com.MonarchUniversity.MonarchUniversity.Exception.ResponseNotFoundException;
import com.MonarchUniversity.MonarchUniversity.Payload.*;
import com.MonarchUniversity.MonarchUniversity.Repositories.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class FeeScheduleService {
    private final LevelRepository levelRepository;
    private final ProgramRepository programRepository;
    private final FeeScheduleRepo feeScheduleRepo;
    private final FeeTypeRepo feeTypeRepo;
    private final FeeScheduleItemRepo feeScheduleItemRepo;

    private BigDecimal calculateTotal(List<FeeScheduleItem> items) {
        return items.stream()
                .map(FeeScheduleItem::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }


    public FeeResTypeDto createFeeType(FeeReqTypeDto dto){
        FeeType feeType = new FeeType();
        String feeName = dto.getFeeName().trim();
        if(feeTypeRepo.existsByName(feeName)){
            throw new ResponseNotFoundException("Such a fee name already exists");
        }
        feeType.setName(feeName);
        FeeType savedFeeType = feeTypeRepo.save(feeType);


        return new FeeResTypeDto(savedFeeType.getId(), savedFeeType.getName());
    }

    public List<FeeResTypeDto> getAllFeeTypes(){
        return feeTypeRepo.findAll().stream().map(d-> new FeeResTypeDto(d.getId(), d.getName()))
                .collect(Collectors.toList());
    }


    //    Create

    public FeeScheduleResDto createFeeSchedule(FeeScheduleReqDto dto) {

        Program program = programRepository.findById(dto.getProgramId())
                .orElseThrow(() -> new ResponseNotFoundException("No such program"));

        Level level = levelRepository.findById(dto.getLevelId())
                .orElseThrow(() -> new ResponseNotFoundException("No such level"));

        if (!level.getProgram().getId().equals(program.getId())) {
            throw new ResponseNotFoundException("This level is not associated with this program");
        }

        if(feeScheduleRepo.existsByLevelAndProgram(level,program)){
            throw new ResponseNotFoundException("Such fee type for level and program has been created, consider editing");
        }

        FeeSchedule feeSchedule = new FeeSchedule();
        feeSchedule.setLevel(level);
        feeSchedule.setProgram(program);

        feeScheduleRepo.save(feeSchedule);

        List<FeeScheduleItem> savedItems = new ArrayList<>();
        List<FeeItemResDto> feeItems = new ArrayList<>();

        for (FeeAmountDto feeDto : dto.getFeeAmountDtoList()) {

            FeeType feeType = feeTypeRepo.findById(feeDto.getFeeTypeId())
                    .orElseThrow(() -> new ResponseNotFoundException("Invalid fee type id"));

            if (feeDto.getAmount().compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Fee amount must be >= 0");
            }

            FeeScheduleItem item = new FeeScheduleItem();
            item.setFeeSchedule(feeSchedule);
            item.setFeeType(feeType);
            item.setAmount(feeDto.getAmount());

            feeScheduleItemRepo.save(item);
            savedItems.add(item);

            feeItems.add(new FeeItemResDto(
                    feeType.getId(),
                    feeType.getName(),
                    feeDto.getAmount()
            ));
        }

        BigDecimal totalAmount = calculateTotal(savedItems);

        return new FeeScheduleResDto(
                feeSchedule.getId(),
                level.getLevelNumber(),
                level.getId(),
                program.getProgramName(),
                program.getId(),
                totalAmount,
                feeItems
        );
    }

    //    Get All
    public List<FeeScheduleResDto> getAllFeeSchedules() {

        List<FeeSchedule> schedules = feeScheduleRepo.findAll();
        List<FeeScheduleResDto> response = new ArrayList<>();

        for (FeeSchedule schedule : schedules) {

            List<FeeScheduleItem> items =
                    feeScheduleItemRepo.findByFeeSchedule(schedule);

            BigDecimal totalAmount = calculateTotal(items);

            List<FeeItemResDto> feeItems = items.stream()
                    .map(item -> new FeeItemResDto(
                            item.getFeeType().getId(),
                            item.getFeeType().getName(),
                            item.getAmount()
                    ))
                    .toList();

            response.add(new FeeScheduleResDto(
                    schedule.getId(),
                    schedule.getLevel().getLevelNumber(),
                    schedule.getLevel().getId(),
                    schedule.getProgram().getProgramName(),
                    schedule.getProgram().getId(),
                    totalAmount,
                    feeItems
            ));
        }

        return response;
    }

//    Update

public FeeScheduleResDto updateFeeSchedule(Long feeScheduleId, FeeScheduleReqDto dto) {

    FeeSchedule feeSchedule = feeScheduleRepo.findById(feeScheduleId)
            .orElseThrow(() -> new ResponseNotFoundException("Fee schedule not found"));

    Program program = programRepository.findById(dto.getProgramId())
            .orElseThrow(() -> new ResponseNotFoundException("No such program"));

    Level level = levelRepository.findById(dto.getLevelId())
            .orElseThrow(() -> new ResponseNotFoundException("No such level"));

    if (!level.getProgram().getId().equals(program.getId())) {
        throw new ResponseNotFoundException("This level is not associated with this program");
    }

    feeSchedule.setProgram(program);
    feeSchedule.setLevel(level);
    feeScheduleRepo.save(feeSchedule);

    List<FeeScheduleItem> oldItems = feeScheduleItemRepo.findByFeeSchedule(feeSchedule);
    feeScheduleItemRepo.deleteAll(oldItems);

    List<FeeScheduleItem> savedItems = new ArrayList<>();
    List<FeeItemResDto> feeItems = new ArrayList<>();

    for (FeeAmountDto feeDto : dto.getFeeAmountDtoList()) {

        FeeType feeType = feeTypeRepo.findById(feeDto.getFeeTypeId())
                .orElseThrow(() -> new ResponseNotFoundException("Invalid fee type id"));

        if (feeDto.getAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Fee amount must be >= 0");
        }

        FeeScheduleItem item = new FeeScheduleItem();
        item.setFeeSchedule(feeSchedule);
        item.setFeeType(feeType);
        item.setAmount(feeDto.getAmount());

        feeScheduleItemRepo.save(item);
        savedItems.add(item);

        feeItems.add(new FeeItemResDto(
                feeType.getId(),
                feeType.getName(),
                feeDto.getAmount()
        ));
    }

    BigDecimal totalAmount = calculateTotal(savedItems);

    return new FeeScheduleResDto(
            feeSchedule.getId(),
            level.getLevelNumber(),
            level.getId(),
            program.getProgramName(),
            program.getId(),
            totalAmount,
            feeItems
    );
}

}
