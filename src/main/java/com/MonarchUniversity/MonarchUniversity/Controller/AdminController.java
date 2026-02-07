package com.MonarchUniversity.MonarchUniversity.Controller;

import java.util.List;
import java.util.Map;

import com.MonarchUniversity.MonarchUniversity.Payload.*;
import com.MonarchUniversity.MonarchUniversity.Impl.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

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
    private final TimetableService timetableService;
    private final StudentPaymentService studentPaymentService;
    private final CourseService courseService;


    @GetMapping("/faculties-management")
	    public ResponseEntity<List<FacultyResponseDto>> getAllFacultiesViaManagement() {
	        List<FacultyResponseDto> faculties = supermanagementService.findAllFaculties();
	        return ResponseEntity.ok(faculties);
	    }
	    
	    @GetMapping("/faculties-management/{facultyId}/departments")
	    public ResponseEntity<List<DepartmentDto>> getDepartmentsByFaculty(@PathVariable Long facultyId) {
	        List<DepartmentDto> departments = supermanagementService.findDepartments(facultyId);
	        return ResponseEntity.ok(departments);
	    }
//
//	    @GetMapping("/department-management/{programId}/programs")
//	    public ResponseEntity<List<ProgramDto>> getProgramsByDepartment(@PathVariable Long programId){
//	    	List<ProgramDto> programs = supermanagementService.findPrograms(programId);
//	    	return ResponseEntity.ok(programs);
//	    }
// IMPORTANT
//	    @GetMapping("/levels-management/{programId}/levels")
//	    public ResponseEntity<?> getAllLevelsViaProgram(@PathVariable Long programId){
//	    	return ResponseEntity.ok(supermanagementService.findLevelsViaProgram(programId));
//	    }
	
	@PostMapping("/create-student-profile")
	public ResponseEntity<?> createStudentProfile(@RequestBody StudentProfileRequestDto dto){
		return ResponseEntity.ok(studentProfileService.createStudentProfile(dto));
	}

	@GetMapping("/create-student-profile")
	public ResponseEntity<?> getStudents(){
		return ResponseEntity.ok(studentProfileService.getAllStudents());
	}

    @GetMapping("/create-student-profile/{departmentId}/{levelId}")
    public ResponseEntity<?> getStudentsByLevelAndDepartment(@PathVariable Long departmentId, @PathVariable Long levelId){
        return ResponseEntity.ok(studentProfileService.getStudentByDepartmentAndLevel(departmentId,levelId));
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

    @PutMapping("/create-session/{id}")
    public ResponseEntity<?> updateSessionById(@PathVariable Long id,@Valid @RequestBody SessionRequestDto dto){
        return ResponseEntity.ok(sessionAndSemesterService.updateSession(id,dto));
    }

    @GetMapping("/create-session/{id}")
    public ResponseEntity<?> getSessionById(@PathVariable Long id){
        return ResponseEntity.ok(sessionAndSemesterService.getSessionById(id));
    }

    @PostMapping("/create-semester")
    public ResponseEntity<?> createSemester(@Valid @RequestBody SemesterRequestDto dto){
         return ResponseEntity.ok(sessionAndSemesterService.createSemester(dto));
    }

    @GetMapping("/create-semester")
    public ResponseEntity<?> getSemesters(){
        return ResponseEntity.ok(sessionAndSemesterService.getAllSemester());
    }

    @PutMapping("/create-semester/{id}")
    public ResponseEntity<?> updateSemesterById(@PathVariable Long id,@Valid @RequestBody SemesterRequestDto dto){
        return ResponseEntity.ok(sessionAndSemesterService.updateSemester(id,dto));
    }

    @GetMapping("/create-semester/{id}")
    public ResponseEntity<?> getSemesterById(@PathVariable Long id){
        return ResponseEntity.ok(sessionAndSemesterService.getSemesterById(id));
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

    //    new
    @PostMapping("/upload-timetable")
    @Transactional
    public ResponseEntity<Map<String, Object>> uploadTimetableExcel(
            @RequestParam("file") MultipartFile file) throws Exception {

        Map<String, Object> result = timetableService.uploadTimetableExcel(file);
        return ResponseEntity.ok(result);
    }
    @GetMapping("/upload-timetable")
    public ResponseEntity<TimetableResponseDto> getTimetable(
            @RequestParam("departmentName") String departmentName,
            @RequestParam("level") String levelNumber,
            @RequestParam("semester") String semester,
            @RequestParam("academicYear") Integer academicYear
    ) {

        TimetableResponseDto response =
                timetableService.getTimetable(departmentName, levelNumber, semester, academicYear);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/timetable/{id}/pdf")
    public ResponseEntity<byte[]> downloadTimetablePdf(@PathVariable Long id) {

        byte[] pdf = timetableService.generateTimetablePdf(id);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=timetable.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }


    @GetMapping("/student-payment-list/{levelId}/{departmentId}")
    public List<StudentPaymentListDto> getStudentPaymentViaProgramAndLevel(@PathVariable Long levelId, @PathVariable Long departmentId){
        return studentPaymentService.getALlStudentsPayment(levelId,departmentId);
    }

    @PatchMapping("/student-payments/{studentId}/scholarship")
    public StudentPaymentListDto applyScholarship(
            @PathVariable Long studentId,
            @RequestBody StudentPaymentListDto request
    ) {
        return studentPaymentService.applyScholarship(
                studentId,
                request.getScholarshipPercentage()
        );
    }
// new
    @GetMapping("/get-current-semester-and-session")
    public SemesterResponseDto getCurrentSemesterAndSession(){
        return sessionAndSemesterService.getCurrentSemesterAndSession();
    }

}

