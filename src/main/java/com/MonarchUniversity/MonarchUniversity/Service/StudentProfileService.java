package com.MonarchUniversity.MonarchUniversity.Service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.MonarchUniversity.MonarchUniversity.Entity.Department;
import com.MonarchUniversity.MonarchUniversity.Entity.Faculty;
import com.MonarchUniversity.MonarchUniversity.Entity.Level;
import com.MonarchUniversity.MonarchUniversity.Entity.Program;
import com.MonarchUniversity.MonarchUniversity.Entity.Role;
import com.MonarchUniversity.MonarchUniversity.Entity.StudentProfile;
import com.MonarchUniversity.MonarchUniversity.Entity.User;
import com.MonarchUniversity.MonarchUniversity.Exception.ResponseNotFoundException;
import com.MonarchUniversity.MonarchUniversity.Payload.StudentProfileRequestDto;
import com.MonarchUniversity.MonarchUniversity.Payload.StudentProfileResponseDto;
import com.MonarchUniversity.MonarchUniversity.Repositories.DepartmentRepository;
import com.MonarchUniversity.MonarchUniversity.Repositories.FacultyRepository;
import com.MonarchUniversity.MonarchUniversity.Repositories.LevelRepository;
import com.MonarchUniversity.MonarchUniversity.Repositories.ProgramRepository;
import com.MonarchUniversity.MonarchUniversity.Repositories.RoleRepository;
import com.MonarchUniversity.MonarchUniversity.Repositories.StudentProfileRepo;
import com.MonarchUniversity.MonarchUniversity.Repositories.UserRepository;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class StudentProfileService {

    private final ProgramRepository programRepo;
    private final FacultyRepository facultyRepo;
    private final DepartmentRepository departmentRepo;
    private final LevelRepository levelRepo;
    private final StudentProfileRepo studentProfileRepo;
    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final PasswordEncoder encoder;
    
    @Transactional
    public StudentProfileResponseDto createStudentProfile(StudentProfileRequestDto dto) {

        // Fetch related entities
        Program program = programRepo.findById(dto.getProgramId())
                .orElseThrow(() -> new ResponseNotFoundException("No such Program"));
        Faculty faculty = facultyRepo.findById(dto.getFacultyId())
                .orElseThrow(() -> new ResponseNotFoundException("No such Faculty"));
        Department department = departmentRepo.findById(dto.getDepartmentId())
                .orElseThrow(() -> new ResponseNotFoundException("No such Department"));
        Level level = levelRepo.findById(dto.getLevelId())
                .orElseThrow(() -> new ResponseNotFoundException("No such Level"));

        // Validate relationships
        if (!department.getFaculty().getId().equals(faculty.getId())) {
            throw new ResponseNotFoundException("This department does not belong to this faculty");
        }

        if (!program.getDepartment().getId().equals(department.getId())) {
            throw new ResponseNotFoundException("This program does not belong to this department");
        }

        if (!level.getProgram().getId().equals(program.getId())) {
            throw new ResponseNotFoundException("This level does not belong to this program");
        }

        String matric = dto.getMatricNumber();
        if ((matric == null || matric.isBlank()) && dto.isAutoGenerateMatric()) {
            matric = generateMatricNumber(program);
        } else if (matric == null || matric.isBlank()) {
            throw new ResponseNotFoundException(
                    "Matric number is required or set autoGenerateMatric = true");
        }

        // Check if matric number is unique
        userRepo.findByUsername(matric)
                .ifPresent(user -> {
                    throw new ResponseNotFoundException("A student with this matric number already exists");
                });


        if (userRepo.existsByUsername(dto.getEmailAddress())) {
            throw new ResponseNotFoundException("This email already exists");
        }
        StudentProfile student = new StudentProfile();
        student.setFirstName(dto.getFirstName());
        student.setMiddleName(dto.getMiddleName());
        student.setLastName(dto.getLastName());
        student.setGender(dto.getGender());
        student.setDateOfBirth(dto.getDateOfBirth());
        student.setNationality(dto.getNationality());
        student.setStateOfOrigin(dto.getStateOfOrigin());
        student.setLga(dto.getLga());
        student.setProgram(program);
        student.setFaculty(faculty);
        student.setDepartment(department);
        student.setLevel(level);
        student.setAdmissionYear(dto.getAdmissionYear());
        student.setMatricNumber(matric);
        student.setModeOfEntry(dto.getModeOfEntry());
        student.setEmailAddress(dto.getEmailAddress());
        student.setPhoneNumber(dto.getPhoneNumber());
        student.setHomeAddress(dto.getHomeAddress());
        

        // Prepare response DTO
        StudentProfileResponseDto response = new StudentProfileResponseDto();
        response.setId(student.getId());
        response.setFirstName(student.getFirstName());
        response.setMiddleName(student.getMiddleName());
        response.setLastName(student.getLastName());
        response.setGender(student.getGender());
        response.setDateOfBirth(student.getDateOfBirth());
        response.setNationality(student.getNationality());
        response.setStateOfOrigin(student.getStateOfOrigin());
        response.setLga(student.getLga());
        response.setProgramName(student.getProgram().getProgramName());
        response.setFacultyName(student.getFaculty().getFacultyName());
        response.setDepartmentName(student.getDepartment().getDepartmentName());
        response.setLevelName(student.getLevel().getLevelNumber());
        response.setAdmissionYear(student.getAdmissionYear());
        response.setMatricNumber(student.getMatricNumber());
        response.setModeOfEntry(student.getModeOfEntry());
        response.setEmailAddress(student.getEmailAddress());
        response.setPhoneNumber(student.getPhoneNumber());
        response.setHomeAddress(student.getHomeAddress());
        
        	

    User user = new User();
    user.setUsername(matric);
    user.setPassword(encoder.encode(dto.getLastName()));

    Role role = roleRepo.findByName("STUDENT").orElseThrow(() -> new ResponseNotFoundException("No such role id"));
    user.getRoles().add(role);
    userRepo.save(user);
    
    student.setUser(user);
     
    studentProfileRepo.save(student);

    response.setStatus(user.isEnabled() ? "Active" : "Suspended");

        return response;
    }
    
    public List<StudentProfileResponseDto> getAllStudents() {
        return studentProfileRepo.findAll().stream()
                .map(student -> {
                    StudentProfileResponseDto dto = new StudentProfileResponseDto();
                    dto.setId(student.getId());
                    dto.setFirstName(student.getFirstName());
                    dto.setMiddleName(student.getMiddleName());
                    dto.setLastName(student.getLastName());
                    dto.setGender(student.getGender());
                    dto.setDateOfBirth(student.getDateOfBirth());
                    dto.setNationality(student.getNationality());
                    dto.setStateOfOrigin(student.getStateOfOrigin());
                    dto.setLga(student.getLga());
                    dto.setProgramName(student.getProgram().getProgramName());
                    dto.setFacultyName(student.getFaculty().getFacultyName());
                    dto.setDepartmentName(student.getDepartment().getDepartmentName());
                    dto.setLevelName(student.getLevel().getLevelNumber());
                    dto.setAdmissionYear(student.getAdmissionYear());
                    dto.setMatricNumber(student.getMatricNumber());
                    dto.setModeOfEntry(student.getModeOfEntry());
                    dto.setEmailAddress(student.getEmailAddress());
                    dto.setPhoneNumber(student.getPhoneNumber());
                    dto.setHomeAddress(student.getHomeAddress());
                    dto.setStatus(
                    	    student.getUser() != null && student.getUser().isEnabled() ? "Active" : "Suspended"
                    	);

                    return dto;
                })
                .toList();
    }
    
//    @Transactional
//    public void deleteStudent(Long id) {
//        StudentProfile student = studentProfileRepo.findById(id)
//                .orElseThrow(() -> new ResponseNotFoundException("No student found with id: " + id));
//
//        // Delete associated user account if exists
//        userRepo.findByUsername(student.getMatricNumber())
//                .ifPresent(userRepo::delete);
//
//        studentProfileRepo.delete(student);
//    }
//    
    @Transactional
    public StudentProfileResponseDto updateStudent(Long id, StudentProfileRequestDto dto) {
        StudentProfile student = studentProfileRepo.findById(id)
                .orElseThrow(() -> new ResponseNotFoundException("No student found with id: " + id));

        Program program = programRepo.findById(dto.getProgramId())
                .orElseThrow(() -> new ResponseNotFoundException("No such Program"));
        Faculty faculty = facultyRepo.findById(dto.getFacultyId())
                .orElseThrow(() -> new ResponseNotFoundException("No such Faculty"));
        Department department = departmentRepo.findById(dto.getDepartmentId())
                .orElseThrow(() -> new ResponseNotFoundException("No such Department"));
        Level level = levelRepo.findById(dto.getLevelId())
                .orElseThrow(() -> new ResponseNotFoundException("No such Level"));

        // Validate relationships
        if (!department.getFaculty().getId().equals(faculty.getId()))
            throw new ResponseNotFoundException("This department does not belong to this faculty");
        if (!program.getDepartment().getId().equals(department.getId()))
            throw new ResponseNotFoundException("This program does not belong to this department");
        if (!level.getProgram().getId().equals(program.getId()))
            throw new ResponseNotFoundException("This level does not belong to this program");

        // Update fields
        student.setFirstName(dto.getFirstName());
        student.setMiddleName(dto.getMiddleName());
        student.setLastName(dto.getLastName());
        student.setGender(dto.getGender());
        student.setDateOfBirth(dto.getDateOfBirth());
        student.setNationality(dto.getNationality());
        student.setStateOfOrigin(dto.getStateOfOrigin());
        student.setLga(dto.getLga());
        student.setProgram(program);
        student.setFaculty(faculty);
        student.setDepartment(department);
        student.setLevel(level);
        student.setAdmissionYear(dto.getAdmissionYear());
        student.setModeOfEntry(dto.getModeOfEntry());
        student.setEmailAddress(dto.getEmailAddress());
        student.setPhoneNumber(dto.getPhoneNumber());
        student.setHomeAddress(dto.getHomeAddress());

        // Update matric number if changed or auto-generate
        String matric = dto.getMatricNumber();
        if ((matric == null || matric.isBlank()) && dto.isAutoGenerateMatric()) {
            matric = generateMatricNumber(program);
        } else if (matric == null || matric.isBlank()) {
            throw new ResponseNotFoundException("Matric number is required or set autoGenerateMatric = true");
        }

        student.setMatricNumber(matric);

        studentProfileRepo.save(student);

        // Prepare response DTO
        StudentProfileResponseDto response = new StudentProfileResponseDto();
        response.setId(student.getId());
        response.setFirstName(student.getFirstName());
        response.setMiddleName(student.getMiddleName());
        response.setLastName(student.getLastName());
        response.setGender(student.getGender());
        response.setDateOfBirth(student.getDateOfBirth());
        response.setNationality(student.getNationality());
        response.setStateOfOrigin(student.getStateOfOrigin());
        response.setLga(student.getLga());
        response.setProgramName(student.getProgram().getProgramName());
        response.setFacultyName(student.getFaculty().getFacultyName());
        response.setDepartmentName(student.getDepartment().getDepartmentName());
        response.setLevelName(student.getLevel().getLevelNumber());
        response.setAdmissionYear(student.getAdmissionYear());
        response.setMatricNumber(student.getMatricNumber());
        response.setModeOfEntry(student.getModeOfEntry());
        response.setEmailAddress(student.getEmailAddress());
        response.setPhoneNumber(student.getPhoneNumber());
        response.setHomeAddress(student.getHomeAddress());

        return response;
    }

    private String generateMatricNumber(Program program) {
        String matric;
        boolean isUnique = false;

        do {
            // Generate a matric number
            String prefix = "MNU-" + program.getId();
            String random = String.format("%05d", (int) (Math.random() * 100000));
            matric = prefix + "-" + random;

            // Check if it already exists in the database
            isUnique = studentProfileRepo.findByMatricNumber(matric).isEmpty();

        } while (!isUnique); // repeat if not unique

        return matric;
    }

    @Transactional
    public StudentProfileResponseDto toggleStudentStatus(Long id) {
        StudentProfile student = studentProfileRepo.findById(id)
                .orElseThrow(() -> new ResponseNotFoundException("No such student"));
        
        User user = student.getUser();

        // Toggle the user's enabled status
     
        
        user.setEnabled(!user.isEnabled());

        // Explicitly save the user to ensure the change persists
        userRepo.save(user);

        // Build the response DTO
        StudentProfileResponseDto response = new StudentProfileResponseDto();
        response.setId(student.getId());
        response.setFirstName(student.getFirstName());
        response.setMiddleName(student.getMiddleName());
        response.setLastName(student.getLastName());
        response.setGender(student.getGender());
        response.setDateOfBirth(student.getDateOfBirth());
        response.setNationality(student.getNationality());
        response.setStateOfOrigin(student.getStateOfOrigin());
        response.setLga(student.getLga());
        response.setProgramName(student.getProgram().getProgramName());
        response.setFacultyName(student.getFaculty().getFacultyName());
        response.setDepartmentName(student.getDepartment().getDepartmentName());
        response.setLevelName(student.getLevel().getLevelNumber());
        response.setAdmissionYear(student.getAdmissionYear());
        response.setMatricNumber(student.getMatricNumber());
        response.setModeOfEntry(student.getModeOfEntry());
        response.setEmailAddress(student.getEmailAddress());
        response.setPhoneNumber(student.getPhoneNumber());
        response.setHomeAddress(student.getHomeAddress());

        return response;
    }
}
