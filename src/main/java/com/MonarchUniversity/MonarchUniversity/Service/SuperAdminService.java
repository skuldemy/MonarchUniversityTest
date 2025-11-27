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
	
	
	@Transactional
	public LecturerResponseDto createNewUser(LecturerRequestDto dto) {
		 userRepo.findByUsername(dto.getEmailAddress())
		        .ifPresent(user -> {
		            throw new ResponseNotFoundException("User already exists");
		        });
		User user = new User();
		user.setUsername(dto.getEmailAddress());
		user.setPassword(enconder.encode( dto.getPassword()));
		
		userRepo.save(user);
		
		Faculty faculty = facultyRepo.findById(dto.getFacultyId()).orElseThrow(()-> new ResponseNotFoundException("No such faculty"));
		Department department = departmentRepo.findById(dto.getDepartmentId()).orElseThrow(()-> new ResponseNotFoundException("No such department available"));
		
		if(!department.getFaculty().getId().equals(faculty.getId())) {
			 throw new ResponseNotFoundException("Department does not belong to the selected Faculty");
		}
		
		Role role = roleRepo.findById(dto.getRoleId()).orElseThrow(()-> new ResponseNotFoundException("No such role id"));
		
		  if (role.getName().equalsIgnoreCase("STUDENT")) {
		        throw new ResponseNotFoundException(
		            "Student accounts cannot be created from this panel. Only technical users can be created."
		        );
		    }
		
		List<Program> courseList = programRepo.findAllById(dto.getCourses());
		
		if(courseList.size() != dto.getCourses().size()) {
			throw new ResponseNotFoundException("One or more selected courses do not exist");
		}
		
		for(Program course : courseList) {
			if(!course.getDepartment().getId().equals(department.getId())) {
				throw new ResponseNotFoundException(
		                "Course " + course.getProgramName() + " does not belong to this department"
		            );
			}
		}
		
		LecturerProfile lecturerProfile = new LecturerProfile();
		lecturerProfile.setUser(user);
		lecturerProfile.setFullName(dto.getFullName());
		lecturerProfile.setRole(role);
		lecturerRepo.save(lecturerProfile);
		
		LecturerResponseDto response = new LecturerResponseDto();
	    response.setFullName(lecturerProfile.getFullName());
	    response.setEmailAddress(user.getUsername());
	    response.setOnBoard("offline"); // or "online" depending on your logic
	    response.setRoleName(role.getName());
	    response.setDepartmentName(department.getDepartmentName());
	    response.setCoursesOffering(
	            courseList.stream().map(Program::getProgramName).toList()
	    );
	    
	    if(user.isEnabled()) {
	    response.setStatus("enabled");
	    }
	    else {
	    	response.setStatus("disabled");
	    }
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
