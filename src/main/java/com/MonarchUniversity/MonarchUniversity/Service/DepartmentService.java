package com.MonarchUniversity.MonarchUniversity.Service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.MonarchUniversity.MonarchUniversity.Entity.Department;
import com.MonarchUniversity.MonarchUniversity.Entity.Faculty;
import com.MonarchUniversity.MonarchUniversity.Exception.ResponseForbiddenException;
import com.MonarchUniversity.MonarchUniversity.Exception.ResponseNotFoundException;
import com.MonarchUniversity.MonarchUniversity.Payload.DepartmentDto;
import com.MonarchUniversity.MonarchUniversity.Repositories.DepartmentRepository;
import com.MonarchUniversity.MonarchUniversity.Repositories.FacultyRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DepartmentService {

    private final DepartmentRepository departmentRepo;
    private final FacultyRepository facultyRepo;


    public long numberOfDepartments() {
    	return departmentRepo.count();
    }
    
    public DepartmentDto createDepartment(DepartmentDto dto) {
        if (dto.getDepartmentName() == null || dto.getDepartmentName().isBlank()) {
            throw new ResponseForbiddenException("Department name is required");
        }
        if (dto.getDepartmentCode() == null || dto.getDepartmentCode().isBlank()) {
            throw new ResponseForbiddenException("Department code is required");
        }
        if (dto.getFacultyId() == null) {
            throw new ResponseForbiddenException("Faculty ID is required");
        }

        Faculty faculty = facultyRepo.findById(dto.getFacultyId())
                .orElseThrow(() -> new ResponseNotFoundException("Faculty not found"));

        Department dept = new Department();
        if(departmentRepo.existsByDepartmentNameIgnoreCase(dto.getDepartmentName())){
            throw new ResponseNotFoundException("Department name already exists");
        }
        dept.setDepartmentName(dto.getDepartmentName());
        dept.setDepartmentCode(dto.getDepartmentCode());
        dept.setFaculty(faculty);
        dept.setDepartmentDescription(dto.getDepartmentDescription());
        dept.setOfficeLocation(dto.getOfficeLocation());
        dept.setEstablishedYear(dto.getEstablishedYear());

        Department saved = departmentRepo.save(dept);

        return new DepartmentDto(
                saved.getId(),
                saved.getDepartmentName(),
                saved.getDepartmentCode(),
                saved.getFaculty().getId(),
                saved.getFaculty().getFacultyName(),
                saved.getDepartmentDescription(),
                saved.getOfficeLocation(),
                saved.getEstablishedYear()
        );
    }

    public DepartmentDto editDepartment(Long id, DepartmentDto dto) {
        Department dept = departmentRepo.findById(id)
                .orElseThrow(() -> new ResponseNotFoundException("No such Department Id"));

        if (dto.getDepartmentName() == null || dto.getDepartmentName().isBlank()) {
            throw new ResponseForbiddenException("Department name is required");
        }
        if (dto.getDepartmentCode() == null || dto.getDepartmentCode().isBlank()) {
            throw new ResponseForbiddenException("Department code is required");
        }
        if (dto.getFacultyId() == null) {
            throw new ResponseForbiddenException("Faculty ID is required");
        }

        Faculty faculty = facultyRepo.findById(dto.getFacultyId())
                .orElseThrow(() -> new ResponseNotFoundException("Faculty not found"));

        if(departmentRepo.existsByDepartmentNameIgnoreCase(dto.getDepartmentName())){
            throw new ResponseNotFoundException("Department name already exists");
        }
        dept.setDepartmentName(dto.getDepartmentName());
        dept.setDepartmentCode(dto.getDepartmentCode());
        dept.setFaculty(faculty);
        dept.setDepartmentDescription(dto.getDepartmentDescription());
        dept.setOfficeLocation(dto.getOfficeLocation());
        dept.setEstablishedYear(dto.getEstablishedYear());

        Department saved = departmentRepo.save(dept);

        return new DepartmentDto(
                saved.getId(),
                saved.getDepartmentName(),
                saved.getDepartmentCode(),
                saved.getFaculty().getId(),
                saved.getFaculty().getFacultyName(),
                saved.getDepartmentDescription(),
                saved.getOfficeLocation(),
                saved.getEstablishedYear()
        );
    }

    public String deleteDepartment(Long id) {
        Department dept = departmentRepo.findById(id)
                .orElseThrow(() -> new ResponseNotFoundException("No such Department Id"));

        departmentRepo.delete(dept);
        return "Department successfully deleted";
    }

    @Cacheable(value = "departments")
    public List<DepartmentDto> getAllDepartments() {
        return departmentRepo.findAll()
                .stream()
                .map(d -> new DepartmentDto(
                        d.getId(),
                        d.getDepartmentName(),
                        d.getDepartmentCode(),
                        d.getFaculty() != null ? d.getFaculty().getId() : null,
                        d.getFaculty() != null ? d.getFaculty().getFacultyName() : null,
                        d.getDepartmentDescription(),
                        d.getOfficeLocation(),
                        d.getEstablishedYear()
                ))
                .collect(Collectors.toList());
    }
}
