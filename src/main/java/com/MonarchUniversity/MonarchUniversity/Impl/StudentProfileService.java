package com.MonarchUniversity.MonarchUniversity.Impl;

import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDate;


import com.MonarchUniversity.MonarchUniversity.Model.Department;
import com.MonarchUniversity.MonarchUniversity.Model.Faculty;
import com.MonarchUniversity.MonarchUniversity.Model.Level;
import com.MonarchUniversity.MonarchUniversity.Model.Program;
import com.MonarchUniversity.MonarchUniversity.Model.Role;
import com.MonarchUniversity.MonarchUniversity.Model.StudentProfile;
import com.MonarchUniversity.MonarchUniversity.Model.User;
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
import org.springframework.web.multipart.MultipartFile;

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
    public Map<String, Object> uploadStudentsExcel(MultipartFile file) throws Exception {

        if (!file.getOriginalFilename().endsWith(".xlsx")) {
            throw new ResponseNotFoundException("Only Excel (.xlsx) files are allowed");
        }

        int inserted = 0;
        int skipped = 0;

        Workbook workbook = new XSSFWorkbook(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;

            String firstName = getCellValue(row.getCell(0));
            String middleName = getCellValue(row.getCell(1));
            String lastName = getCellValue(row.getCell(2));
            String gender = getCellValue(row.getCell(3));
            String email = getCellValue(row.getCell(5));
            String phone = getCellValue(row.getCell(6));
            String matricFromExcel = getCellValue(row.getCell(13));

            if (firstName == null || lastName == null) {
                skipped++;
                continue;
            }

            // Use getCellValue() to safely extract strings from Excel
            String facultyName = getCellValue(row.getCell(7));
            String departmentName = getCellValue(row.getCell(8));
            String levelNumber = getCellValue(row.getCell(9));

            // Lookup hierarchy safely, throw ResponseNotFoundException if missing
            Faculty faculty = facultyRepo.findByFacultyName(facultyName)
                    .orElseThrow(() -> new ResponseNotFoundException(
                            "Faculty not found: " + facultyName));

            Department department = departmentRepo
                    .findByDepartmentNameAndFaculty(departmentName, faculty)
                    .orElseThrow(() -> new ResponseNotFoundException(
                            "Department '" + departmentName +
                                    "' does not belong to faculty '" + facultyName + "'"));


            Level level = levelRepo
                    .findByLevelNumberAndDepartment(levelNumber, department)
                    .orElseThrow(() -> new ResponseNotFoundException(
                            "Level '" + levelNumber +
                                    "' does not belong to department '" + departmentName + "'"));

            // Generate matric number if missing
            String matric = matricFromExcel;
            if (matric == null || matric.isBlank()) {
                matric = generateMatricNumber(department);
            }

            // Skip duplicates
            if (studentProfileRepo.findByMatricNumber(matric).isPresent() ||
                    userRepo.existsByUsername(matric) ||
                    userRepo.existsByUsername(email)) {
                skipped++;
                continue;
            }

            StudentProfile student = new StudentProfile();
            student.setFirstName(firstName);
            student.setMiddleName(middleName);
            student.setLastName(lastName);
            student.setGender(gender);
            student.setEmailAddress(email);
            student.setPhoneNumber(phone);
            student.setFaculty(faculty);
            student.setDateOfBirth(parseLocalDate(row.getCell(4)));
            student.setDepartment(department);
            student.setLevel(level);
            student.setAdmissionYear(parseLocalDate(row.getCell(11)));
            student.setModeOfEntry(getCellValue(row.getCell(12)));
            student.setMatricNumber(matric);


            // Create User
            User user = new User();
            user.setUsername(matric);
            user.setPassword(encoder.encode(lastName));
            Role role = roleRepo.findByName("STUDENT")
                    .orElseThrow(() -> new ResponseNotFoundException("Role STUDENT not found"));
            user.getRoles().add(role);
            userRepo.save(user);

            student.setUser(user);
            studentProfileRepo.save(student);

            inserted++;
        }

        workbook.close();

        return Map.of(
                "inserted", inserted,
                "skipped", skipped,
                "total", inserted + skipped
        );
    }

    private String getCellValue(Cell cell) {
        if (cell == null) return null;

        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> {
                if (DateUtil.isCellDateFormatted(cell)) {
                    yield cell.getLocalDateTimeCellValue().toLocalDate().toString();
                } else {
                    yield String.valueOf((long) cell.getNumericCellValue());
                }
            }
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> null;
        };
    }

    private LocalDate parseLocalDate(Cell cell) {
        if (cell == null) return null;

        switch (cell.getCellType()) {
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getLocalDateTimeCellValue().toLocalDate();
                }
                int year = (int) cell.getNumericCellValue();
                return LocalDate.of(year, 1, 1);
            case STRING:
                return LocalDate.parse(cell.getStringCellValue().trim());
            default:
                throw new ResponseNotFoundException("Invalid date format in Excel");
        }
    }

    @Transactional
    public StudentProfileResponseDto createStudentProfile(StudentProfileRequestDto dto) {

        // Fetch related entities

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

        if (!level.getDepartment().getId().equals(department.getId())) {
            throw new ResponseNotFoundException("This level does not belong to this department");
        }

        String matric = dto.getMatricNumber();
        if ((matric == null || matric.isBlank()) && dto.isAutoGenerateMatric()) {
            matric = generateMatricNumber(department);
        } else if (matric == null || matric.isBlank()) {
            throw new ResponseNotFoundException(
                    "Matric number is required or set autoGenerateMatric = true");
        }

        // Check if matric number is unique
        userRepo.findByUsername(matric)
                .ifPresent(user -> {
                    throw new ResponseNotFoundException("A student with this matric number already exists");
                });
/// student matric

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

    public List<StudentProfileResponseDto> getStudentByDepartmentAndLevel(Long departmentId, Long levelId ){
        Department department = departmentRepo.findById(departmentId)
                .orElseThrow(()-> new ResponseNotFoundException("No such department"));
        Level level = levelRepo.findById(levelId)
                .orElseThrow(()-> new ResponseNotFoundException("No such Level"));
       List<StudentProfile>  studentProfiles = studentProfileRepo.findByDepartmentAndLevel(department, level);
        return studentProfiles.stream().map(
                student -> {
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
                }
        ).toList();
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

        Faculty faculty = facultyRepo.findById(dto.getFacultyId())
                .orElseThrow(() -> new ResponseNotFoundException("No such Faculty"));
        Department department = departmentRepo.findById(dto.getDepartmentId())
                .orElseThrow(() -> new ResponseNotFoundException("No such Department"));
        Level level = levelRepo.findById(dto.getLevelId())
                .orElseThrow(() -> new ResponseNotFoundException("No such Level"));

        // Validate relationships
        if (!department.getFaculty().getId().equals(faculty.getId()))
            throw new ResponseNotFoundException("This department does not belong to this faculty");
        if (!level.getDepartment().getId().equals(department.getId()))
            throw new ResponseNotFoundException("This level does not belong to this department");

        // Update fields
        student.setFirstName(dto.getFirstName());
        student.setMiddleName(dto.getMiddleName());
        student.setLastName(dto.getLastName());
        student.setGender(dto.getGender());
        student.setDateOfBirth(dto.getDateOfBirth());
        student.setNationality(dto.getNationality());
        student.setStateOfOrigin(dto.getStateOfOrigin());
        student.setLga(dto.getLga());
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
            matric = generateMatricNumber(department);
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

    private String generateMatricNumber(Department department) {
        String matric;
        boolean isUnique = false;

        do {
            // Generate a matric number
            String prefix = "MNU-" + department.getId();
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

    public List<StudentProfileResponseDto> findStudentsInDepartmentAndLevel(Long departmentId,Long levelId){

        Department department = departmentRepo.findById(departmentId)
                .orElseThrow(() -> new ResponseNotFoundException("No such Department"));
        Level level = levelRepo.findById(levelId)
                .orElseThrow(() -> new ResponseNotFoundException("No such Level"));


        List<StudentProfile> studentProfileList = studentProfileRepo
                .findByDepartmentAndLevel(department, level);
        return studentProfileList.stream()
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

//    public StudentProfileResponseDto getStudentsInProgramAndLevel(Long programId, Long levelId){
//
//        Program program = programRepo.findById(programId)
//                .orElseThrow(() -> new ResponseNotFoundException("No such Program"));
//
//        Level level = levelRepo.findById(levelId)
//                .orElseThrow(() -> new ResponseNotFoundException("No such Level"));
//
//       return studentProfileRepo.findByProgramAndLevel(program,level)
//               .stream().map(student -> {
//                   StudentProfileResponseDto dto = new StudentProfileResponseDto();
//                   dto.setId(student.getId());
//                   dto.setFirstName(student.getFirstName());
//                   dto.setMiddleName(student.getMiddleName());
//                   dto.setLastName(student.getLastName());
//                   dto.setGender(student.getGender());
//                   dto.setDateOfBirth(student.getDateOfBirth());
//                   dto.setNationality(student.getNationality());
//                   dto.setStateOfOrigin(student.getStateOfOrigin());
//                   dto.setLga(student.getLga());
//                   dto.setProgramName(student.getProgram().getProgramName());
//                   dto.setFacultyName(student.getFaculty().getFacultyName());
//                   dto.setDepartmentName(student.getDepartment().getDepartmentName());
//                   dto.setLevelName(student.getLevel().getLevelNumber());
//                   dto.setAdmissionYear(student.getAdmissionYear());
//                   dto.setMatricNumber(student.getMatricNumber());
//                   dto.setModeOfEntry(student.getModeOfEntry());
//                   dto.setEmailAddress(student.getEmailAddress());
//                   dto.setPhoneNumber(student.getPhoneNumber());
//                   dto.setHomeAddress(student.getHomeAddress());
//                   dto.setStatus(
//                           student.getUser() != null && student.getUser().isEnabled() ? "Active" : "Suspended"
//                   );
//
//                   return dto;
//               })
//               .toList();
//    }
//


}
