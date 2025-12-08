package com.MonarchUniversity.MonarchUniversity.Controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.MonarchUniversity.MonarchUniversity.Payload.DepartmentDto;
import com.MonarchUniversity.MonarchUniversity.Payload.FacultyDto;
import com.MonarchUniversity.MonarchUniversity.Payload.LecturerResponseDto;
import com.MonarchUniversity.MonarchUniversity.Payload.ProgramDto;
import com.MonarchUniversity.MonarchUniversity.Payload.StudentProfileRequestDto;
import com.MonarchUniversity.MonarchUniversity.Service.StudentProfileService;
import com.MonarchUniversity.MonarchUniversity.Service.SuperAdminService;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/admin")
public class AdminController {
	private final StudentProfileService studentProfileService;
	private final SuperAdminService supermanagementService;
	
	 @GetMapping("/faculties-management")
	    public ResponseEntity<List<FacultyDto>> getAllFacultiesViaManagement() {
	        List<FacultyDto> faculties = supermanagementService.findAllFaculties();
	        return ResponseEntity.ok(faculties);
	    }
	    
	    @GetMapping("/faculties-management/{facultyId}/departments")
	    public ResponseEntity<List<DepartmentDto>> getDepartmentsByFaculty(@PathVariable Long facultyId) {
	        List<DepartmentDto> departments = supermanagementService.findDepartments(facultyId);
	        return ResponseEntity.ok(departments);
	    }
	   
	    @GetMapping("/department-management/{programId}/programs")
	    public ResponseEntity<List<ProgramDto>> getProgramsByDepartment(@PathVariable Long programId){
	    	List<ProgramDto> programs = supermanagementService.findPrograms(programId);
	    	return ResponseEntity.ok(programs);
	    }

	    @GetMapping("/levels-management/{programId}/levels")
	    public ResponseEntity<?> getAllLevelsViaProgram(@PathVariable Long programId){
	    	return ResponseEntity.ok(supermanagementService.findLevelsViaProgram(programId));
	    }
	
	@PostMapping("/create-student-profile")
	public ResponseEntity<?> createStudentProfile(@RequestBody StudentProfileRequestDto dto){
		return ResponseEntity.ok(studentProfileService.createStudentProfile(dto));
	}
	@GetMapping("/create-student-profile")
	public ResponseEntity<?> getStudents(){
		return ResponseEntity.ok(studentProfileService.getAllStudents());
	}
	@PutMapping("/create-student-profile/{id}")
	public ResponseEntity<?> updateStudentsViaId(@PathVariable Long id, @RequestBody StudentProfileRequestDto dto){
		return ResponseEntity.ok(studentProfileService.updateStudent(id, dto));
	}
//	new
	
//	Still need to work on this
	@PatchMapping("/create-student-profile/{id}/toggle")
	public ResponseEntity<?> toggleStudentProfile(@PathVariable Long id){
		return ResponseEntity.ok(studentProfileService.toggleStudentStatus(id));
	}
	
	@GetMapping("/admin-management")
    public ResponseEntity<List<LecturerResponseDto>> getAllLecturers() {
        List<LecturerResponseDto> lecturers = supermanagementService.getAllLecturers();
        return ResponseEntity.ok(lecturers);
    }
	
}

