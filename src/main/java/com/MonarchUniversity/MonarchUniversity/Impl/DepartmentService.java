package com.MonarchUniversity.MonarchUniversity.Impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.MonarchUniversity.MonarchUniversity.Model.Department;
import com.MonarchUniversity.MonarchUniversity.Model.Faculty;
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
        if(departmentRepo.existsByDepartmentCodeIgnoreCase(dto.getDepartmentCode())){
            throw new ResponseNotFoundException("Department code already exists");
        }
        dept.setDepartmentCode(dto.getDepartmentCode());
        dept.setFaculty(faculty);
        dept.setDepartmentDescription(dto.getDepartmentDescription());
        dept.setOfficeLocation(dto.getOfficeLocation());
        dept.setEstablishedYear(dto.getEstablishedYear());

        Department saved = departmentRepo.save(dept);

        return mapToDto(saved);
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

        if(departmentRepo.existsByDepartmentNameIgnoreCaseAndIdNot(dto.getDepartmentName(),id)){
            throw new ResponseNotFoundException("Department name already exists");
        }
        if(departmentRepo.existsByDepartmentCodeIgnoreCaseAndIdNot(dto.getDepartmentCode(),id)){
            throw new ResponseNotFoundException("Department code already exists");
        }
        dept.setDepartmentName(dto.getDepartmentName());
        dept.setDepartmentCode(dto.getDepartmentCode());
        dept.setFaculty(faculty);
        dept.setDepartmentDescription(dto.getDepartmentDescription());
        dept.setOfficeLocation(dto.getOfficeLocation());
        dept.setEstablishedYear(dto.getEstablishedYear());

        Department saved = departmentRepo.save(dept);

        return mapToDto(saved);
    }

    public DepartmentDto getDepartmentById(Long id){
        Department department = departmentRepo.findById(id)
                .orElseThrow(()-> new ResponseNotFoundException("No such department"));
        return mapToDto(department);
    }

    public String deleteDepartment(Long id) {
        Department dept = departmentRepo.findById(id)
                .orElseThrow(() -> new ResponseNotFoundException("No such Department Id"));

        departmentRepo.delete(dept);
        return "Department successfully deleted";
    }


    public List<DepartmentDto> getAllDepartments() {
        return departmentRepo.findAll()
                .stream()
                .map(d -> mapToDto(d))
                .collect(Collectors.toList());
    }
    public DepartmentDto mapToDto(Department saved){
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
}
