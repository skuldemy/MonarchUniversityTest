package com.MonarchUniversity.MonarchUniversity.Controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.MonarchUniversity.MonarchUniversity.Payload.DepartmentDto;
import com.MonarchUniversity.MonarchUniversity.Payload.FacultyDto;
import com.MonarchUniversity.MonarchUniversity.Payload.ProgramDto;
import com.MonarchUniversity.MonarchUniversity.Service.DepartmentService;
import com.MonarchUniversity.MonarchUniversity.Service.FacultyService;
import com.MonarchUniversity.MonarchUniversity.Service.ProgramService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/super-admin")
public class SuperAdminAccountController {
	private final FacultyService facultyService;
	private final DepartmentService departmentService;
	private final ProgramService programService;
	
	@PostMapping("/faculty")
	public ResponseEntity<?> createFaculty(@RequestBody @Valid FacultyDto dto){
		 FacultyDto createdFaculty = facultyService.createFaculty(dto);
		    return ResponseEntity.status(201).body(createdFaculty);
	}
	
	@GetMapping("/faculty")
	public ResponseEntity<?> getAllFaculties(){
		return ResponseEntity.ok(facultyService.getAllFaculties());
	}
	
	@PutMapping("/faculty/{id}")
	public ResponseEntity<?> editFaculty(@PathVariable Long id, @RequestBody @Valid FacultyDto dto){
		return ResponseEntity.ok(facultyService.editFaculty(id, dto));
	}
	
	@DeleteMapping("/faculty/{id}")
	public ResponseEntity<?> deleteFaculty(@PathVariable Long id){
		return ResponseEntity.ok(facultyService.deleteFaculty(id));
	}
//	Department
	@PostMapping("/department")
    public ResponseEntity<?> createDepartment(@RequestBody @Valid DepartmentDto dto) {
        DepartmentDto createdDepartment = departmentService.createDepartment(dto);
        return ResponseEntity.status(201).body(createdDepartment);
    }

    @GetMapping("/department")
    public ResponseEntity<List<DepartmentDto>> getAllDepartments() {
        return ResponseEntity.ok(departmentService.getAllDepartments());
    }

    @PutMapping("/department/{id}")
    public ResponseEntity<?> editDepartment(@PathVariable Long id, @RequestBody @Valid DepartmentDto dto) {
        DepartmentDto updatedDepartment = departmentService.editDepartment(id, dto);
        return ResponseEntity.ok(updatedDepartment);
    }

    @DeleteMapping("/department/{id}")
    public ResponseEntity<?> deleteDepartment(@PathVariable Long id) {
        String message = departmentService.deleteDepartment(id);
        return ResponseEntity.ok(message);
    }
    
//    Program
    @PostMapping("/program")
    public ResponseEntity<ProgramDto> createProgram(@RequestBody @Valid ProgramDto dto) {
        ProgramDto created = programService.createProgram(dto);
        return ResponseEntity.status(201).body(created);
    }

    @GetMapping("/program")
    public ResponseEntity<List<ProgramDto>> getAllPrograms() {
        return ResponseEntity.ok(programService.getAllPrograms());
    }

    @PutMapping("/program/{id}")
    public ResponseEntity<ProgramDto> editProgram(@PathVariable Long id, @RequestBody @Valid ProgramDto dto) {
        return ResponseEntity.ok(programService.editProgram(id, dto));
    }

    @DeleteMapping("/program/{id}")
    public ResponseEntity<String> deleteProgram(@PathVariable Long id) {
        return ResponseEntity.ok(programService.deleteProgram(id));
    }

}
