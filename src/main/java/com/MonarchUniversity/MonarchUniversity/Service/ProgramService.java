package com.MonarchUniversity.MonarchUniversity.Service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.MonarchUniversity.MonarchUniversity.Entity.Department;
import com.MonarchUniversity.MonarchUniversity.Entity.Program;
import com.MonarchUniversity.MonarchUniversity.Exception.ResponseForbiddenException;
import com.MonarchUniversity.MonarchUniversity.Exception.ResponseNotFoundException;
import com.MonarchUniversity.MonarchUniversity.Payload.ProgramDto;
import com.MonarchUniversity.MonarchUniversity.Repositories.DepartmentRepository;
import com.MonarchUniversity.MonarchUniversity.Repositories.ProgramRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProgramService {

    private final ProgramRepository programRepo;
    private final DepartmentRepository departmentRepo;

    public long numberOfPrograms() {
    	return programRepo.count();
    }
    
    public ProgramDto createProgram(ProgramDto dto) {
        Department department = departmentRepo.findById(dto.getDepartmentId())
                .orElseThrow(() -> new ResponseNotFoundException("Department not found"));

        Program program = new Program();
        program.setProgramName(dto.getProgramName());
        program.setProgramCode(dto.getProgramCode());
        program.setDepartment(department);
        program.setDuration(dto.getDuration());
        program.setProgramType(dto.getProgramType());
        program.setModeOfStudy(dto.getModeOfStudy());
        program.setEntryRequirements(dto.getEntryRequirements());

        Program saved = programRepo.save(program);

        return mapToDto(saved);
    }

    public ProgramDto editProgram(Long id, ProgramDto dto) {
        Program program = programRepo.findById(id)
                .orElseThrow(() -> new ResponseNotFoundException("Program not found"));

        if (dto.getProgramName() == null || dto.getProgramName().isBlank()) {
            throw new ResponseForbiddenException("Program name is required");
        }
        if (dto.getProgramCode() == null || dto.getProgramCode().isBlank()) {
            throw new ResponseForbiddenException("Program code is required");
        }
        if (dto.getDepartmentId() == null) {
            throw new ResponseForbiddenException("Department ID is required");
        }

        Department department = departmentRepo.findById(dto.getDepartmentId())
                .orElseThrow(() -> new ResponseNotFoundException("Department not found"));

        program.setProgramName(dto.getProgramName());
        program.setProgramCode(dto.getProgramCode());
        program.setDepartment(department);
        program.setDuration(dto.getDuration());
        program.setProgramType(dto.getProgramType());
        program.setModeOfStudy(dto.getModeOfStudy());
        program.setEntryRequirements(dto.getEntryRequirements());

        Program saved = programRepo.save(program);
        return mapToDto(saved);
    }

    public String deleteProgram(Long id) {
        Program program = programRepo.findById(id)
                .orElseThrow(() -> new ResponseNotFoundException("Program not found"));
        programRepo.delete(program);
        return "Program successfully deleted";
    }

    @Cacheable("programs")
    public List<ProgramDto> getAllPrograms() {
        return programRepo.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private ProgramDto mapToDto(Program program) {
        return new ProgramDto(
                program.getId(),
                program.getProgramName(),
                program.getProgramCode(),
                program.getDepartment() != null ? program.getDepartment().getId() : null,
                program.getDuration(),
                program.getProgramType(),
                program.getModeOfStudy(),
                program.getEntryRequirements()
        );
    }
}
