package com.MonarchUniversity.MonarchUniversity.Service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.MonarchUniversity.MonarchUniversity.Entity.PortalAction;
import com.MonarchUniversity.MonarchUniversity.Entity.PortalManagement;
import com.MonarchUniversity.MonarchUniversity.Exception.ResponseNotFoundException;
import com.MonarchUniversity.MonarchUniversity.Payload.PortalActionDto;
import com.MonarchUniversity.MonarchUniversity.Payload.PortalManagementRequestDto;
import com.MonarchUniversity.MonarchUniversity.Payload.PortalManagementResponseDto;
import com.MonarchUniversity.MonarchUniversity.Repositories.PortalActionRepository;
import com.MonarchUniversity.MonarchUniversity.Repositories.PortalManagementRepo;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor

public class PortalManagementService {
	private final PortalManagementRepo portalManagementRepo;
	private final PortalActionRepository portalActionRepo;
	
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

}
