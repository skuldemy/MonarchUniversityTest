package com.MonarchUniversity.MonarchUniversity.Impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.MonarchUniversity.MonarchUniversity.Model.FeeType;
import com.MonarchUniversity.MonarchUniversity.Model.PortalSchedule;
import com.MonarchUniversity.MonarchUniversity.Payload.*;

import com.MonarchUniversity.MonarchUniversity.Repositories.FeeTypeRepo;
import com.MonarchUniversity.MonarchUniversity.Repositories.PortalScheduleRepo;
import org.springframework.stereotype.Service;

import com.MonarchUniversity.MonarchUniversity.Model.PortalAction;
import com.MonarchUniversity.MonarchUniversity.Model.PortalManagement;
import com.MonarchUniversity.MonarchUniversity.Exception.ResponseNotFoundException;
import com.MonarchUniversity.MonarchUniversity.Repositories.PortalActionRepository;
import com.MonarchUniversity.MonarchUniversity.Repositories.PortalManagementRepo;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor

public class PortalManagementService {
	private final PortalManagementRepo portalManagementRepo;
	private final PortalActionRepository portalActionRepo;
    private final PortalScheduleRepo portalScheduleRepo;
    private final FeeTypeRepo feeTypeRepo;
	
	public List<PortalActionDto> findAllPortalActions(){
		return portalActionRepo.findAll().stream().map(p -> new PortalActionDto(p.getId(), p.getActionName())).toList();
	}
	
	public PortalManagementResponseDto createPortalManagement(PortalManagementRequestDto dto) {
		
	    boolean portalExists = portalManagementRepo
	            .existsByPortalNameAndAcademicYearAndSemester(
	                    dto.getPortalName(),
	                    dto.getAcademicYear(),
	                    dto.getSemester()
	            );
	    
	    if(portalExists) {
	        throw new ResponseNotFoundException(
	                "Portal for this name, academic year and semester already exists"
	        );
	    }

		
		PortalManagement portalManagement = new PortalManagement();
		portalManagement.setPortalName(dto.getPortalName());
		portalManagement.setDescription(dto.getDescription());
		portalManagement.setAcademicYear(dto.getAcademicYear());
		portalManagement.setSemester(dto.getSemester());
		portalManagement.setOpeningDate(dto.getOpeningDate());
		portalManagement.setClosingDate(dto.getClosingDate());
		
		List<PortalAction> portalActionList = portalActionRepo.findAllById(dto.getAllowedActions());
		if(portalActionList.size() != dto.getAllowedActions().size()) {
			throw new ResponseNotFoundException("One or more selected actions do not exist");
		}
		portalManagement.setAllowedActions(portalActionList);
	    portalManagement.setEnabled(true); 

	    
	    portalManagementRepo.save(portalManagement);
	
	    PortalManagementResponseDto response = new PortalManagementResponseDto();
	    response.setId(portalManagement.getId());
	    response.setPortalName(portalManagement.getPortalName());
	    response.setDescription(portalManagement.getDescription());
	    response.setAcademicYear(portalManagement.getAcademicYear());
	    response.setSemester(portalManagement.getSemester());
	    response.setOpeningDate(portalManagement.getOpeningDate());
	    response.setClosingDate(portalManagement.getClosingDate());
	    
	    response.setAllowedActions(portalActionList.stream().map(PortalAction::getActionName).toList());
	    response.setEnabled(portalManagement.isEnabled());

	    
	    return response;
	}
	
	public List<PortalManagementResponseDto> getAllPortals() {
	    List<PortalManagement> portals = portalManagementRepo.findAll();

	    return portals.stream().map(portal -> {
	        PortalManagementResponseDto dto = new PortalManagementResponseDto();
	        dto.setId(portal.getId());
	        dto.setPortalName(portal.getPortalName());
	        dto.setDescription(portal.getDescription());
	        dto.setAcademicYear(portal.getAcademicYear());
	        dto.setSemester(portal.getSemester());
	        dto.setOpeningDate(portal.getOpeningDate());
	        dto.setClosingDate(portal.getClosingDate());
	        dto.setEnabled(portal.isEnabled());
	        dto.setAllowedActions(
	                portal.getAllowedActions()
	                        .stream()
	                        .map(PortalAction::getActionName)
	                        .toList()
	        );
	        return dto;
	    }).toList();
	}

	public PortalManagementResponseDto updatePortal(Long id, PortalManagementRequestDto dto) {

	    PortalManagement portal = portalManagementRepo.findById(id)
	            .orElseThrow(() -> new ResponseNotFoundException("Portal not found"));

	    // Check for duplicate (except itself)
	    boolean exists = portalManagementRepo
	            .existsByPortalNameAndAcademicYearAndSemesterAndIdNot(
	                    dto.getPortalName(),
	                    dto.getAcademicYear(),
	                    dto.getSemester(),
	                    id
	            );

	    if (exists) {
	        throw new ResponseNotFoundException(
	                "Another portal with the same name, academic year, and semester already exists"
	        );
	    }

	    // Validate actions
	    List<PortalAction> actions = portalActionRepo.findAllById(dto.getAllowedActions());

	    if (actions.size() != dto.getAllowedActions().size()) {
	        throw new ResponseNotFoundException("One or more selected actions do not exist");
	    }

	    // Update fields
	    portal.setPortalName(dto.getPortalName());
	    portal.setDescription(dto.getDescription());
	    portal.setAcademicYear(dto.getAcademicYear());
	    portal.setSemester(dto.getSemester());
	    portal.setOpeningDate(dto.getOpeningDate());
	    portal.setClosingDate(dto.getClosingDate());
	    portal.setAllowedActions(actions);

	    portalManagementRepo.save(portal);

	    // Response
	    PortalManagementResponseDto response = new PortalManagementResponseDto();
	    response.setId(portal.getId());
	    response.setPortalName(portal.getPortalName());
	    response.setDescription(portal.getDescription());
	    response.setAcademicYear(portal.getAcademicYear());
	    response.setSemester(portal.getSemester());
	    response.setOpeningDate(portal.getOpeningDate());
	    response.setClosingDate(portal.getClosingDate());
	    response.setEnabled(portal.isEnabled());
	    response.setAllowedActions(
	            actions.stream().map(PortalAction::getActionName).toList()
	    );

	    return response;
	}

	public String deletePortal(Long id) {
	    PortalManagement portal = portalManagementRepo.findById(id)
	            .orElseThrow(() -> new ResponseNotFoundException("Portal not found"));

	    portalManagementRepo.delete(portal);

	    return "Portal deleted successfully";
	}

	@Transactional
	public PortalManagementResponseDto togglePortalStatus(Long id) {

	    PortalManagement portal = portalManagementRepo.findById(id)
	            .orElseThrow(() -> new ResponseNotFoundException("No such portal exists"));

	    // Toggle logic
	    portal.setEnabled(!portal.isEnabled());

	    portalManagementRepo.save(portal);

	    // Prepare response
	    PortalManagementResponseDto response = new PortalManagementResponseDto();
	    response.setId(portal.getId());
	    response.setPortalName(portal.getPortalName());
	    response.setDescription(portal.getDescription());
	    response.setAcademicYear(portal.getAcademicYear());
	    response.setSemester(portal.getSemester());
	    response.setOpeningDate(portal.getOpeningDate());
	    response.setClosingDate(portal.getClosingDate());
	    response.setEnabled(portal.isEnabled());

	    response.setAllowedActions(
	            portal.getAllowedActions()
	                    .stream()
	                    .map(a -> a.getActionName())
	                    .toList()
	    );

	    return response;
	}

    public List<PortalActionDto> getPortalByRegisterCourses(){
        return portalActionRepo.findAllByActionName("Register Courses").stream()
                .map(r -> new PortalActionDto(r.getId(), r.getActionName()))
                .collect(Collectors.toList());
    }

    public PortalScheduleResDto createPortalSchedule(PortalScheduleReqDto dto) {
        PortalSchedule portalSchedule = new PortalSchedule();

        FeeType feeType = feeTypeRepo.findById(dto.getFeeTypeId())
                .orElseThrow(() -> new ResponseNotFoundException("No such portal action"));

        if (portalScheduleRepo.existsByFeeType(feeType)) {
            throw new ResponseNotFoundException("This portal already exists, might consider updating");
        }

        if (dto.getStartDate().isBefore(LocalDate.now())) {
            throw new ResponseNotFoundException("Start date cannot be in the past");
        }

        portalSchedule.setFeeType(feeType);
        portalSchedule.setDescription(dto.getDescription());
        portalSchedule.setStartDate(dto.getStartDate());
        portalSchedule.setEndDate(dto.getEndDate());


        portalSchedule.setStatus(
                !LocalDate.now().isBefore(dto.getStartDate()) &&
                        !LocalDate.now().isAfter(dto.getEndDate())
        );

        PortalSchedule saved = portalScheduleRepo.save(portalSchedule);

        return convertToRes(saved);
    }

    public List<PortalScheduleResDto> getAllPortalSchedules(){
        return new ArrayList<>(portalScheduleRepo.findAll().stream()
                .map(d -> new PortalScheduleResDto(d.getId(),
                        d.getFeeType().getName(),
                        d.getDescription(),
                        d.getStartDate(),
                        d.getEndDate(),
                        d.getStatus().toString()
                        ))
                .collect(Collectors.toList()));
    }


    public PortalScheduleResDto togglePortalStatus(Long id, Boolean status) {
        PortalSchedule portalSchedule = portalScheduleRepo.findById(id)
                .orElseThrow(() -> new ResponseNotFoundException("Schedule not found"));

        portalSchedule.setStatus(status);

        PortalSchedule updated = portalScheduleRepo.save(portalSchedule);
        return convertToRes(updated);
    }

    public PortalScheduleResDto updatePortalSchedule(Long id, PortalScheduleReqDto dto) {
        PortalSchedule portalSchedule = portalScheduleRepo.findById(id)
                .orElseThrow(() -> new ResponseNotFoundException("Schedule not found"));

        if (dto.getStartDate() != null) {
            if (dto.getStartDate().isBefore(LocalDate.now())) {
                throw new ResponseNotFoundException("Start date cannot be in the past");
            }
            portalSchedule.setStartDate(dto.getStartDate());
        }

        if (dto.getEndDate() != null) {
            if (dto.getEndDate().isBefore(portalSchedule.getStartDate())) {
                throw new ResponseNotFoundException("End date cannot be before start date");
            }
            portalSchedule.setEndDate(dto.getEndDate());
        }

        // Recalculate status
        portalSchedule.setStatus(
                !LocalDate.now().isBefore(portalSchedule.getStartDate()) &&
                        !LocalDate.now().isAfter(portalSchedule.getEndDate())
        );

        PortalSchedule updated = portalScheduleRepo.save(portalSchedule);
        return convertToRes(updated);
    }


    private PortalScheduleResDto convertToRes(PortalSchedule schedule) {
        PortalScheduleResDto dto = new PortalScheduleResDto();
        dto.setId(schedule.getId());
        dto.setFeeType(schedule.getFeeType().getName());
        dto.setDescription(schedule.getDescription());
        dto.setStartDate(schedule.getStartDate());
        dto.setEndDate(schedule.getEndDate());
        dto.setStatus(schedule.getStatus() ? "Active" : "InActive");
        return dto;
    }


}
