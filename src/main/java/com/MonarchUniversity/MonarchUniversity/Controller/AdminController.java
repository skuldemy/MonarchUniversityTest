package com.MonarchUniversity.MonarchUniversity.Controller;

import java.util.List;
import java.util.Map;

import com.MonarchUniversity.MonarchUniversity.Payload.*;
import com.MonarchUniversity.MonarchUniversity.Service.FeeScheduleService;
import com.MonarchUniversity.MonarchUniversity.Service.SessionAndSemesterService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import com.MonarchUniversity.MonarchUniversity.Service.StudentProfileService;
import com.MonarchUniversity.MonarchUniversity.Service.SuperAdminService;

import lombok.AllArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@RestController
@AllArgsConstructor
@RequestMapping("/admin")
public class AdminController {
	private final StudentProfileService studentProfileService;
	private final SuperAdminService supermanagementService;
    private final SessionAndSemesterService sessionAndSemesterService;
    private final FeeScheduleService feeScheduleService;


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

	@GetMapping("/admin-management")
    public ResponseEntity<List<LecturerResponseDto>> getAllLecturers() {
        List<LecturerResponseDto> lecturers = supermanagementService.getAllLecturers();
        return ResponseEntity.ok(lecturers);
    }


    @PostMapping("/create-session")
    public ResponseEntity<?> createSession(@Valid @RequestBody SessionRequestDto dto){
        return ResponseEntity.ok(sessionAndSemesterService.createSession(dto));
    }

    @GetMapping("/create-session")
    public ResponseEntity<?> getAllSessions(){
        return ResponseEntity.ok(sessionAndSemesterService.getAllSession());
    }


    @PostMapping("/create-semester")
    public ResponseEntity<?> createSemester(@Valid @RequestBody SemesterRequestDto dto){
         return ResponseEntity.ok(sessionAndSemesterService.createSemester(dto));
    }

    @GetMapping("/create-semester")
    public ResponseEntity<?> getSemesters(){
        return ResponseEntity.ok(sessionAndSemesterService.getAllSemester());
    }

    // new

    @GetMapping("/fee-Type")
    public ResponseEntity<?> getFeeTypes(){
        return ResponseEntity.ok(feeScheduleService.getAllFeeTypes());
    }

    @PostMapping("/fee-Type")
    public ResponseEntity<?> createFeeType(@RequestBody FeeReqTypeDto dto){
        return ResponseEntity.ok(feeScheduleService.createFeeType(dto));
    }

    @PostMapping("/create-fee-schedule")
    public ResponseEntity<?> createFeeSchedule(@RequestBody FeeScheduleReqDto dto){
        return ResponseEntity.ok(feeScheduleService.createFeeSchedule(dto));
    }

    @GetMapping("/create-fee-schedule")
    public ResponseEntity<?> getFeeSchedule(){
        return ResponseEntity.ok(feeScheduleService.getAllFeeSchedules());
    }

    @PutMapping("/create-fee-schedule/{itemId}")
    public ResponseEntity<?> updateFeeSchedule(@PathVariable Long itemId, @RequestBody FeeScheduleReqDto dto){
        return ResponseEntity.ok(feeScheduleService.updateFeeSchedule(itemId, dto));
    }
//    new
    @PostMapping("/upload-student-details")
    @Transactional
    public ResponseEntity<Map<String, Object>> uploadStudentsExcel(@RequestParam("file") MultipartFile file) throws Exception {
        Map<String, Object> result = studentProfileService.uploadStudentsExcel(file);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @PatchMapping("/create-student-profile/{id}/toggle")
    public ResponseEntity<?> toggleStudentProfile(@PathVariable Long id){
        return ResponseEntity.ok(studentProfileService.toggleStudentStatus(id));
    }

}

