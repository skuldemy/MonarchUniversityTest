package com.MonarchUniversity.MonarchUniversity.Controller;

import java.util.List;

import com.MonarchUniversity.MonarchUniversity.Payload.*;
import com.MonarchUniversity.MonarchUniversity.Impl.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
	private final LevelService levelService;
	private final SuperAdminService managementService;
	private final PortalManagementService portalManagementService;
	private final UserService userService;
    private final ImpersonateService impersonateService;
    private final SessionAndSemesterService sessionAndSemesterService;
    private final CourseService courseService;
    private final FeeScheduleService feeScheduleService;

	
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


    
    @PostMapping("/level")
    public ResponseEntity<LevelDto> createLevel(@RequestBody @Valid LevelDto dto){
    	LevelDto created = levelService.createLevel(dto);
    	return ResponseEntity.status(201).body(created);
    }
    
    @GetMapping("/level")
    public ResponseEntity<List<LevelDto>> getAllLevels(){
    	return ResponseEntity.ok(levelService.getAllLevels());
    }
    
    @PutMapping("/level/{id}")
    public ResponseEntity<LevelDto> editLevel(@PathVariable Long id, @RequestBody @Valid LevelDto dto){
    	return ResponseEntity.ok(levelService.editLevel(id, dto));
    }
    
    @DeleteMapping("/level/{id}")
    public ResponseEntity<String> deleteLevel(@PathVariable Long id){
    	return ResponseEntity.ok(levelService.deleteLevel(id));
    }
    
    
    @GetMapping("/roles-management")
    public ResponseEntity<?> getRolesExcludingStudent(){
    	return ResponseEntity.ok(managementService.getAllRoles());
    }
    
    @GetMapping("/faculties-management")
    public ResponseEntity<List<FacultyDto>> getAllFacultiesViaManagement() {
        List<FacultyDto> faculties = managementService.findAllFaculties();
        return ResponseEntity.ok(faculties);
    }
    
    @GetMapping("/faculties-management/{facultyId}/departments")
    public ResponseEntity<List<DepartmentDto>> getDepartmentsByFaculty(@PathVariable Long facultyId) {
        List<DepartmentDto> departments = managementService.findDepartments(facultyId);
        return ResponseEntity.ok(departments);
    }
    

    
    @PostMapping("/admin-management")
    public ResponseEntity<LecturerResponseDto> createLecturer(@RequestBody @Valid LecturerRequestDto dto) {
        LecturerResponseDto response = managementService.createNewUser(dto);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/admin-management")
    public ResponseEntity<List<LecturerResponseDto>> getAllLecturers() {
        List<LecturerResponseDto> lecturers = managementService.getAllLecturers();
        return ResponseEntity.ok(lecturers);
    }
    @PutMapping("/admin-management/{id}")
    public ResponseEntity<LecturerResponseDto> updateLecturer(
            @PathVariable Long id,
            @RequestBody @Valid LecturerRequestDto dto) {
        LecturerResponseDto updated = managementService.updateLecturer(id, dto);
        return ResponseEntity.ok(updated);
    }
    
    @DeleteMapping("/admin-management/{id}")
    public ResponseEntity<String> deleteLecturer(@PathVariable Long id) {
    	managementService.deleteLecturer(id);
        return ResponseEntity.ok("Lecturer deleted successfully");
    }
    

  @GetMapping("/department-management/{programId}/programs")
  public ResponseEntity<List<ProgramDto>> getProgramsByDepartment(@PathVariable Long programId){
  	List<ProgramDto> programs = managementService.findPrograms(programId);
  	return ResponseEntity.ok(programs);
  }
  
  @GetMapping("/portal-actions")
  public ResponseEntity<List<PortalActionDto>> getAllPortalActions(){
	  return ResponseEntity.ok( portalManagementService.findAllPortalActions());
  }
  
  @PostMapping("/portal-management")
  public ResponseEntity<PortalManagementResponseDto> createPortal(
          @RequestBody @Valid PortalManagementRequestDto dto
  ) {
      return ResponseEntity.ok(portalManagementService.createPortalManagement(dto));
  }

  @GetMapping("/portal-management")
  public ResponseEntity<List<PortalManagementResponseDto>> getAllPortals() {
      return ResponseEntity.ok(portalManagementService.getAllPortals());
  }

  @PutMapping("/portal-management/{id}")
  public ResponseEntity<PortalManagementResponseDto> updatePortal(
          @PathVariable Long id,
          @RequestBody @Valid PortalManagementRequestDto dto
  ) {
      return ResponseEntity.ok(portalManagementService.updatePortal(id, dto));
  }

  @DeleteMapping("/portal-management/{id}")
  public ResponseEntity<String> deletePortal(@PathVariable Long id) {
      return ResponseEntity.ok(portalManagementService.deletePortal(id));
  }

  @PatchMapping("/portal-management/{id}/toggle")
  public ResponseEntity<PortalManagementResponseDto> togglePortalStatus(@PathVariable Long id) {
      return ResponseEntity.ok(portalManagementService.togglePortalStatus(id));
  }
  
//new apis
  @GetMapping("/number-of-faculties")
  public ResponseEntity<Long> getNumberOfFaculties(){
	  return ResponseEntity.ok(facultyService.numberOfFaculties());
  }
  
  @GetMapping("/number-of-departments")
  public ResponseEntity<Long> getNumberOfDepartments(){
	  return ResponseEntity.ok(departmentService.numberOfDepartments());
  }
  
  @GetMapping("/number-of-programs")
  public ResponseEntity<Long> numberOfPrograms(){
	  return ResponseEntity.ok(programService.numberOfPrograms());
  }
  
  @GetMapping("/number-of-admins")
  public ResponseEntity<Long> numberofadmins(){
	  return ResponseEntity.ok(userService.getNumberOfAdmins());
  }
  
  @GetMapping("/number-of-Hods")
  public ResponseEntity<Long> numberofHods(){
	  return ResponseEntity.ok(userService.getNumberOfHods());
  }

//  new apis

    @GetMapping("/get-all-users")
    public ResponseEntity<?> getAllUsers(){
        return ResponseEntity.ok(impersonateService.getAllUsers());
    }

  @PostMapping("/impersonate-user/{userId}")
    public ResponseEntity<?> impersonateUser(@PathVariable Long userId){
        return ResponseEntity.ok(impersonateService.impersonateUser(userId));
  }

    //    new
    @PostMapping("/create-courses")
    public ResponseEntity<CourseResponseDto> createCourse(
            @RequestBody CourseRequestDto dto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(courseService.createCourse(dto));
    }

    @GetMapping("/create-courses/{programId}/{levelId}")
    public ResponseEntity<List<CourseResponseDto>> getCoursesByProgramAndLevel(
            @PathVariable Long programId,
            @PathVariable Long levelId
    ) {
        return ResponseEntity.ok(
                courseService.getAllCoursesAttachedToProgram(programId, levelId)
        );
    }


    @PutMapping("/create-courses/{courseId}")
    public ResponseEntity<CourseResponseDto> updateCourse(
            @PathVariable Long courseId,
            @RequestBody CourseRequestDto dto
    ) {
        return ResponseEntity.ok(
                courseService.updateCourse(courseId, dto)
        );
    }

//    new
@GetMapping("/fee-Type")
public ResponseEntity<?> getFeeTypes(){
    return ResponseEntity.ok(feeScheduleService.getAllFeeTypes());
}

    @PostMapping("/create-portal-for-registration")
    public ResponseEntity<PortalScheduleResDto> createPortalSchedule(
            @RequestBody PortalScheduleReqDto dto
    ) {
        PortalScheduleResDto resDto = portalManagementService.createPortalSchedule(dto);
        return ResponseEntity.ok(resDto);
    }

    @PutMapping("/create-portal-for-registration/{id}")
    public ResponseEntity<PortalScheduleResDto> updatePortal(
            @PathVariable Long id,
            @RequestBody PortalScheduleReqDto dto) {
        return ResponseEntity.ok(portalManagementService.updatePortalSchedule(id, dto));
    }

    @GetMapping("/create-portal-for-registration")
    public List<PortalScheduleResDto> getAllPortalSchedule(){
        return portalManagementService.getAllPortalSchedules();
    }

    @PatchMapping("/create-portal-for-registration/{id}")
    public ResponseEntity<PortalScheduleResDto> togglePortal(
            @PathVariable Long id,
            @RequestBody PortalScheduleToggleDto dto) {
        return ResponseEntity.ok(portalManagementService.togglePortalStatus(id, dto.getStatus()));
    }

}
