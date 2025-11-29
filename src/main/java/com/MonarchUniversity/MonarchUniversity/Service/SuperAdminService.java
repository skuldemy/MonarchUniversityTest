package com.MonarchUniversity.MonarchUniversity.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.MonarchUniversity.MonarchUniversity.Entity.Department;
import com.MonarchUniversity.MonarchUniversity.Entity.Faculty;
import com.MonarchUniversity.MonarchUniversity.Entity.LecturerProfile;
import com.MonarchUniversity.MonarchUniversity.Entity.Program;
import com.MonarchUniversity.MonarchUniversity.Entity.Role;
import com.MonarchUniversity.MonarchUniversity.Entity.User;
import com.MonarchUniversity.MonarchUniversity.Exception.ResponseNotFoundException;
import com.MonarchUniversity.MonarchUniversity.Payload.DepartmentDto;
import com.MonarchUniversity.MonarchUniversity.Payload.FacultyDto;
import com.MonarchUniversity.MonarchUniversity.Payload.LecturerRequestDto;
import com.MonarchUniversity.MonarchUniversity.Payload.LecturerResponseDto;
import com.MonarchUniversity.MonarchUniversity.Payload.ProgramDto;
import com.MonarchUniversity.MonarchUniversity.Payload.RoleDto;
import com.MonarchUniversity.MonarchUniversity.Payload.UserDto;
import com.MonarchUniversity.MonarchUniversity.Repositories.DepartmentRepository;
import com.MonarchUniversity.MonarchUniversity.Repositories.FacultyRepository;
import com.MonarchUniversity.MonarchUniversity.Repositories.LecturerProfileRepo;
import com.MonarchUniversity.MonarchUniversity.Repositories.ProgramRepository;
import com.MonarchUniversity.MonarchUniversity.Repositories.RoleRepository;
import com.MonarchUniversity.MonarchUniversity.Repositories.UserRepository;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class SuperAdminService {
	private final UserRepository userRepo;
	private final FacultyRepository facultyRepo;
	private final DepartmentRepository departmentRepo;
	private final LecturerProfileRepo lecturerRepo;
	private final RoleRepository roleRepo;
	private final ProgramRepository programRepo;
	private final PasswordEncoder enconder;
	
	public List<FacultyDto> findAllFaculties(){
		return facultyRepo.findAll().stream().map(f -> new FacultyDto(f.getId(), f.getFacultyName())).collect(Collectors.toList());
	}
	
	public List<DepartmentDto> findDepartments(Long id){
		return departmentRepo.findByFacultyId(id).stream()
				.map(d -> new DepartmentDto(d.getId(), d.getDepartmentName())).collect(Collectors.toList());
	}
	
	public List<ProgramDto> findPrograms(Long id){
		return programRepo.findByDepartmentId(id).stream()
				.map(p-> new ProgramDto(p.getId(), p.getProgramName())).collect(Collectors.toList());
	}
	
	public List<RoleDto> getAllRoles(){
		List<Role> roles = roleRepo.findByNameNot("STUDENT");
		return roles.stream().map(r -> new RoleDto(r.getId(), r.getName())).collect(Collectors.toList());
	}
	
	@Transactional
	public LecturerResponseDto createNewUser(LecturerRequestDto dto) {

	    userRepo.findByUsername(dto.getEmailAddress())
	        .ifPresent(user -> {
	            throw new ResponseNotFoundException("User already exists");
	        });

	    User user = new User();
	    user.setUsername(dto.getEmailAddress());
	    user.setPassword(enconder.encode(dto.getPassword()));

	    Role role = roleRepo.findById(dto.getRoleId())
	            .orElseThrow(() -> new ResponseNotFoundException("No such role id"));

	    // Block student creation
	    if (role.getName().equalsIgnoreCase("STUDENT")) {
	        throw new ResponseNotFoundException("Student accounts cannot be created from this panel");
	    }

	    user.getRoles().add(role);
	    userRepo.save(user);

	    // Create profile
	    LecturerProfile lecturerProfile = new LecturerProfile();
	    lecturerProfile.setUser(user);
	    lecturerProfile.setFullName(dto.getFullName());
	    lecturerProfile.setRole(role);

	    Faculty faculty = null;
	    Department department = null;
	    List<Program> courseList = List.of();

	    // -------------------------

	    // 1️⃣ If facultyId provided → validate it
	 // 1️⃣ If facultyId provided → validate it
	    if (dto.getFacultyId() != null && dto.getFacultyId() > 0) {
	        faculty = facultyRepo.findById(dto.getFacultyId())
	                .orElseThrow(() -> new ResponseNotFoundException("No such faculty"));
	    }

	    // 2️⃣ If departmentId provided → validate it
	    if (dto.getDepartmentId() != null && dto.getDepartmentId() > 0) {
	        department = departmentRepo.findById(dto.getDepartmentId())
	                .orElseThrow(() -> new ResponseNotFoundException("No such department"));

	        if (faculty != null && !department.getFaculty().getId().equals(faculty.getId())) {
	            throw new ResponseNotFoundException("Department does not belong to selected faculty");
	        }
	    }

	    // 3️⃣ If courses provided → validate each one and check department consistency if dep is selected
	    if (dto.getCourses() != null && !dto.getCourses().isEmpty()) {

	        courseList = programRepo.findAllById(dto.getCourses());

	        if (courseList.size() != dto.getCourses().size()) {
	            throw new ResponseNotFoundException("One or more selected courses do not exist");
	        }

	        // If department is provided → ensure each course belongs to it
	        if (department != null) {
	            for (Program course : courseList) {
	                if (!course.getDepartment().getId().equals(department.getId())) {
	                    throw new ResponseNotFoundException(
	                            "Course " + course.getProgramName() + " does not belong to this department"
	                    );
	                }
	            }
	        }
	    }

	    // Set courses (maybe empty)
	    lecturerProfile.setCourses(courseList);
	    lecturerRepo.save(lecturerProfile);

	    // Response
	    LecturerResponseDto response = new LecturerResponseDto();
	    response.setFullName(lecturerProfile.getFullName());
	    response.setEmailAddress(user.getUsername());
	    response.setOnBoard("offline");
	    response.setRoleName(role.getName());
	    response.setStatus(user.isEnabled() ? "enabled" : "disabled");

	    response.setDepartmentName(
	            department != null ? department.getDepartmentName() : "N/A"
	    );

	    response.setCoursesOffering(
	            courseList.stream().map(Program::getProgramName).toList()
	    );

	    return response;
	}

	
	public List<LecturerResponseDto> getAllLecturers() {
	    List<LecturerProfile> lecturers = lecturerRepo.findAll();
	    return lecturers.stream().map(lecturer -> {
	        User user = lecturer.getUser();
	        LecturerResponseDto dto = new LecturerResponseDto();
	        dto.setFullName(lecturer.getFullName());
	        dto.setEmailAddress(user.getUsername());
	        dto.setOnBoard("offline");
	        dto.setRoleName(lecturer.getRole().getName());
	        dto.setCoursesOffering(
	                lecturer.getCourses()
	                        .stream()
	                        .map(Program::getProgramName)
	                        .toList()
	            );
	        dto.setStatus(user.isEnabled() ? "enabled" : "disabled");
	        
	        return dto;
	    }).toList();
	}

	public LecturerResponseDto updateLecturer(Long lecturerId, LecturerRequestDto dto) {
	    LecturerProfile lecturer = lecturerRepo.findById(lecturerId)
	        .orElseThrow(() -> new ResponseNotFoundException("Lecturer not found"));

	    User user = lecturer.getUser();

	    // Update basic user info
	    if (dto.getEmailAddress() != null && !dto.getEmailAddress().isBlank()) {
	        user.setUsername(dto.getEmailAddress());
	    }
	    if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
	        user.setPassword(dto.getPassword());
	    }
	    userRepo.save(user);

	    // Update faculty/department
	    if (dto.getFacultyId() != null && dto.getDepartmentId() != null) {
	        Faculty faculty = facultyRepo.findById(dto.getFacultyId())
	            .orElseThrow(() -> new ResponseNotFoundException("Faculty not found"));
	        Department department = departmentRepo.findById(dto.getDepartmentId())
	            .orElseThrow(() -> new ResponseNotFoundException("Department not found"));

	        if (!department.getFaculty().getId().equals(faculty.getId())) {
	            throw new ResponseNotFoundException("Department does not belong to the selected faculty");
	        }
	        // Optionally: update department on lecturer profile if you store it
	    }

	    // Update role
	    if (dto.getRoleId() != null) {
	        Role role = roleRepo.findById(dto.getRoleId())
	            .orElseThrow(() -> new ResponseNotFoundException("Role not found"));
	        if (role.getName().equalsIgnoreCase("STUDENT")) {
	            throw new ResponseNotFoundException("Cannot assign STUDENT role");
	        }
	        lecturer.setRole(role);
	    }

	    // Update full name
	    if (dto.getFullName() != null && !dto.getFullName().isBlank()) {
	        lecturer.setFullName(dto.getFullName());
	    }

	    // Update courses
	    if (dto.getCourses() != null && !dto.getCourses().isEmpty()) {
	        List<Program> courses = programRepo.findAllById(dto.getCourses());
	        if (courses.size() != dto.getCourses().size()) {
	            throw new ResponseNotFoundException("One or more selected courses do not exist");
	        }
	        // Check department consistency
	        // Optional: store courses somewhere
	    }

	    lecturerRepo.save(lecturer);

	    // Return updated response
	    LecturerResponseDto response = new LecturerResponseDto();
	    response.setFullName(lecturer.getFullName());
	    response.setEmailAddress(user.getUsername());
	    response.setOnBoard("offline");
	    response.setRoleName(lecturer.getRole().getName());
	    // Optionally set departmentName and coursesOffering
	    response.setStatus(user.isEnabled() ? "enabled" : "disabled");

	    return response;
	}

	public void deleteLecturer(Long lecturerId) {
	    LecturerProfile lecturer = lecturerRepo.findById(lecturerId)
	        .orElseThrow(() -> new ResponseNotFoundException("Lecturer not found"));

	    // First delete associated user (optional)
	    userRepo.delete(lecturer.getUser());

	    // Then delete lecturer profile
	    lecturerRepo.delete(lecturer);
	}

	
//	@Scheduled(fixedRate = 60000) // every 1 min
//	public void checkInactiveUsers() {
//	    LocalDateTime cutoff = LocalDateTime.now().minusMinutes(5);
//	    List<LecturerProfile> lecturers = lecturerRepo.findAll();
//
//	    for (LecturerProfile lecturer : lecturers) {
//	        if (lecturer.getLastActive() != null &&
//	            lecturer.getLastActive().isBefore(cutoff)) {
//
//	            lecturer.setOnboardStatus("offline");
//	            lecturerRepo.save(lecturer);
//	        }
//	    }
//	}

}
