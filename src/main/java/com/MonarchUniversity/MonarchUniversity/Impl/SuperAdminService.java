package com.MonarchUniversity.MonarchUniversity.Impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.MonarchUniversity.MonarchUniversity.Model.*;
import com.MonarchUniversity.MonarchUniversity.Payload.*;
import com.MonarchUniversity.MonarchUniversity.Repositories.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.MonarchUniversity.MonarchUniversity.Exception.ResponseNotFoundException;

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
	private final LevelRepository levelRepo;
	private final CourseRepository courseRepo;

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
//	public List<LevelDto> findLevelsViaProgram(Long id){
//		return levelRepo.findByProgramId(id).stream().map(l -> new LevelDto(l.getId(), l.getProgram().getId(), l.getProgram().getProgramName(),
//				l.getLevelNumber(), l.getSemester(), l.getCapacity()
//				)).collect(Collectors.toList());
//	}
//
	public List<RoleDto> getAllRoles(){
		List<Role> roles = roleRepo.findByNameNot("STUDENT");
		return roles.stream().map(r -> new RoleDto(r.getId(), r.getName())).collect(Collectors.toList());
	}

    private void validateRoles(Set<Role> roles, LecturerRequestDto dto){
        boolean hasGeneralRole = roles.stream().anyMatch(r ->
                r.getName().equals("SUPER_ADMIN")|| r.getName().equals("ADMIN"));

        boolean hasAcademicRole = roles.stream().anyMatch(r ->
                r.getName().equals("LECTURER") ||
                        r.getName().equals("HOD") ||
                        r.getName().equals("LEVEL_ADVISER") ||
                        r.getName().equals("DEAN")
        );

        if (hasGeneralRole && (dto.getFacultyId() != null || dto.getDepartmentId() != null)) {
            throw new ResponseNotFoundException("Global roles must not have faculty/department");
        }

        if (hasAcademicRole && (dto.getFacultyId() == null || dto.getDepartmentId() == null)) {
            throw new ResponseNotFoundException("Academic roles must have faculty and department");
        }

    }

    @Transactional
    public LecturerResponseDto createNewUser(LecturerRequestDto dto) {


        userRepo.findByUsername(dto.getEmailAddress())
                .ifPresent(u -> {
                    throw new ResponseNotFoundException("User already exists");
                });


        User user = new User();
        user.setUsername(dto.getEmailAddress());
        user.setPassword(enconder.encode(dto.getPassword()));


        Set<Role> roles = dto.getRoleId().stream()
                .map(roleId -> roleRepo.findById(roleId)
                        .orElseThrow(() -> new ResponseNotFoundException("No such role id")))
                .peek(role -> {
                    if (role.getName().equalsIgnoreCase("STUDENT")) {
                        throw new ResponseNotFoundException("Student accounts cannot be created from this panel");
                    }
                })
                .collect(Collectors.toSet());

        validateRoles(roles, dto);
        user.setRoles(roles);
        userRepo.save(user);


        LecturerProfile lecturerProfile = new LecturerProfile();
        lecturerProfile.setUser(user);
        lecturerProfile.setFullName(dto.getFullName());


        List<Course> courseList = List.of();
        if (dto.getCoursesOffering() != null && !dto.getCoursesOffering().isEmpty()) {
            courseList = courseRepo.findAllById(dto.getCoursesOffering());

            if (courseList.size() != dto.getCoursesOffering().size()) {
                throw new ResponseNotFoundException("One or more selected courses do not exist");
            }
        }

        lecturerProfile.setCourses(courseList);
        lecturerRepo.save(lecturerProfile);

        LecturerResponseDto response = new LecturerResponseDto();
        response.setFullName(lecturerProfile.getFullName());
        response.setEmailAddress(user.getUsername());
        response.setOnBoard("offline");

        // Roles as list
        Set<String> roleNames = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
        response.setRoleName(roleNames);

        response.setStatus(user.isEnabled() ? "enabled" : "disabled");

        // Departments dynamically from courses
        Set<String> departments = courseList.stream()
                .map(c -> c.getDepartment().getDepartmentName())
                .collect(Collectors.toSet());
        response.setDepartmentName(departments.isEmpty() ? "N/A" : String.join(", ", departments));


        response.setCoursesOffering(courseList.stream()
                .map(Course::getCourseTitle)
                .toList());

        return response;
    }


    public List<LecturerResponseDto> getAllLecturers() {
        return lecturerRepo.findAll()
                .stream()
                .map(this::buildLecturerResponse)
                .toList();
    }

    public LecturerResponseDto getLecturerById(Long lecturerId) {

        LecturerProfile lecturer = lecturerRepo.findById(lecturerId)
                .orElseThrow(() -> new ResponseNotFoundException("Lecturer not found"));

        return buildLecturerResponse(lecturer);
    }


    @Transactional
    public LecturerResponseDto updateLecturer(Long lecturerId, UpdateLecturerRequestDto dto) {

        LecturerProfile lecturer = lecturerRepo.findById(lecturerId)
                .orElseThrow(() -> new ResponseNotFoundException("Lecturer not found"));

        User user = lecturer.getUser();


        if (dto.getEmailAddress() != null && !dto.getEmailAddress().isBlank()) {
            user.setUsername(dto.getEmailAddress());
        }

        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            user.setPassword(enconder.encode(dto.getPassword()));
        }


        if (dto.getRoleId() != null && !dto.getRoleId().isEmpty()) {

            Set<Role> newRoles = dto.getRoleId().stream()
                    .map(roleId -> roleRepo.findById(roleId)
                            .orElseThrow(() -> new ResponseNotFoundException("No such role id")))
                    .peek(role -> {
                        if (role.getName().equalsIgnoreCase("STUDENT")) {
                            throw new ResponseNotFoundException("Cannot assign STUDENT role");
                        }
                    })
                    .collect(Collectors.toSet());

            validateRoles(newRoles, null);

            user.setRoles(newRoles);
        }

        userRepo.save(user);


        if (dto.getFullName() != null && !dto.getFullName().isBlank()) {
            lecturer.setFullName(dto.getFullName());
        }

        if (dto.getCoursesOffering() != null) {
            List<Course> courses = courseRepo.findAllById(dto.getCoursesOffering());

            if (courses.size() != dto.getCoursesOffering().size()) {
                throw new ResponseNotFoundException("One or more selected courses do not exist");
            }

            lecturer.setCourses(courses);
        }

        lecturerRepo.save(lecturer);

        return buildLecturerResponse(lecturer);
    }

    @Transactional
    public LecturerResponseDto toggleUserStatus(Long lecturerId) {

        LecturerProfile lecturer = lecturerRepo.findById(lecturerId)
                .orElseThrow(() -> new ResponseNotFoundException("Lecturer not found"));

        User user = lecturer.getUser();
        user.setEnabled(!user.isEnabled());
        userRepo.save(user);

        return buildLecturerResponse(lecturer);
    }

    private LecturerResponseDto buildLecturerResponse(LecturerProfile lecturer) {

        User user = lecturer.getUser();

        LecturerResponseDto response = new LecturerResponseDto();
        response.setId(lecturer.getId());
        response.setFullName(lecturer.getFullName());
        response.setEmailAddress(user.getUsername());
        response.setOnBoard("offline");
        response.setStatus(user.isEnabled() ? "enabled" : "disabled");

        // Roles
        Set<String> roleNames = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
        response.setRoleName(roleNames);

        // Courses
        List<Course> courses = lecturer.getCourses();
        response.setCoursesOffering(
                courses.stream()
                        .map(Course::getCourseTitle)
                        .toList()
        );

        // Departments (dynamic from courses)
        Set<String> departments = courses.stream()
                .map(c -> c.getDepartment().getDepartmentName())
                .collect(Collectors.toSet());

        response.setDepartmentName(
                departments.isEmpty() ? "N/A" : String.join(", ", departments)
        );

        return response;
    }


    @Transactional
	public void deleteLecturer(Long lecturerId) {
	    LecturerProfile lecturer = lecturerRepo.findById(lecturerId)
	        .orElseThrow(() -> new ResponseNotFoundException("Lecturer not found"));

	    userRepo.delete(lecturer.getUser());

	    lecturerRepo.delete(lecturer);
	}


}
