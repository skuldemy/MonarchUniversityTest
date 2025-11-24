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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/super-admin")
@Tag(name = "Super Admin")

public class SuperAdminAccountController {
	private final FacultyService facultyService;
	private final DepartmentService departmentService;
	private final ProgramService programService;
	
    @Operation(summary = "1 - Faculty: Create a new faculty", description = "Creates a faculty with all required details")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Faculty created successfully"),
        @ApiResponse(responseCode = "403", description = "Faculty name or code is missing")
    })
    
	@PostMapping("/faculty")
	public ResponseEntity<?> createFaculty(@RequestBody @Valid FacultyDto dto){
		 FacultyDto createdFaculty = facultyService.createFaculty(dto);
		    return ResponseEntity.status(201).body(createdFaculty);
	}
	
    @Operation(summary = "1 - Faculty: Get all faculties", description = "Returns a list of all faculties")
   
    @GetMapping("/faculty")
	public ResponseEntity<?> getAllFaculties(){
		return ResponseEntity.ok(facultyService.getAllFaculties());
	}
	
    @Operation(summary = "1 - Faculty: Edit a faculty", description = "Updates the faculty identified by ID")
    
    @PutMapping("/faculty/{id}")
	public ResponseEntity<?> editFaculty(@PathVariable Long id, @RequestBody @Valid FacultyDto dto){
		return ResponseEntity.ok(facultyService.editFaculty(id, dto));
	}
	
    @Operation(summary = "1 - Faculty: Delete a faculty", description = "Deletes the faculty identified by ID")
    @DeleteMapping("/faculty/{id}")
	public ResponseEntity<?> deleteFaculty(@PathVariable Long id){
		return ResponseEntity.ok(facultyService.deleteFaculty(id));
	}
//	Department
    @Operation(summary = "2 - Department: Create a new department", description = "Creates a department under a specific faculty")
    
    @PostMapping("/department")
    public ResponseEntity<?> createDepartment(@RequestBody @Valid DepartmentDto dto) {
        DepartmentDto createdDepartment = departmentService.createDepartment(dto);
        return ResponseEntity.status(201).body(createdDepartment);
    }

    @Operation(summary = "2 - Department: Get all departments", description = "Returns a list of all departments")
   
    @GetMapping("/department")
    public ResponseEntity<List<DepartmentDto>> getAllDepartments() {
        return ResponseEntity.ok(departmentService.getAllDepartments());
    }

    @Operation(summary = "2 - Department: Edit a department", description = "Updates the department identified by ID")
   
    @PutMapping("/department/{id}")
    public ResponseEntity<?> editDepartment(@PathVariable Long id, @RequestBody @Valid DepartmentDto dto) {
        DepartmentDto updatedDepartment = departmentService.editDepartment(id, dto);
        return ResponseEntity.ok(updatedDepartment);
    }

    @Operation(summary = "2 - Department: Delete a department", description = "Deletes the department identified by ID")
    
    @DeleteMapping("/department/{id}")
    public ResponseEntity<?> deleteDepartment(@PathVariable Long id) {
        String message = departmentService.deleteDepartment(id);
        return ResponseEntity.ok(message);
    }
    
//    Program
    @Operation(summary = "3 - Program: Create a new program", description = "Creates a program under a specific department")
    
    @PostMapping("/program")
    public ResponseEntity<ProgramDto> createProgram(@RequestBody @Valid ProgramDto dto) {
        ProgramDto created = programService.createProgram(dto);
        return ResponseEntity.status(201).body(created);
    }

    @Operation(summary = "3 - Program: Get all programs", description = "Returns a list of all programs")
    
    @GetMapping("/program")
    public ResponseEntity<List<ProgramDto>> getAllPrograms() {
        return ResponseEntity.ok(programService.getAllPrograms());
    }

    @Operation(summary = "3 - Program: Edit a program", description = "Updates the program identified by ID")
    
    @PutMapping("/program/{id}")
    public ResponseEntity<ProgramDto> editProgram(@PathVariable Long id, @RequestBody @Valid ProgramDto dto) {
        return ResponseEntity.ok(programService.editProgram(id, dto));
    }

    @Operation(summary = "3 - Program: Delete a program", description = "Deletes the program identified by ID")
    
    @DeleteMapping("/program/{id}")
    public ResponseEntity<String> deleteProgram(@PathVariable Long id) {
        return ResponseEntity.ok(programService.deleteProgram(id));
    }

}
