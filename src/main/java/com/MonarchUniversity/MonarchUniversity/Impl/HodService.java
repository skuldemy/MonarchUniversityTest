package com.MonarchUniversity.MonarchUniversity.Impl;

import com.MonarchUniversity.MonarchUniversity.Exception.ResponseNotFoundException;
import com.MonarchUniversity.MonarchUniversity.Model.*;
import com.MonarchUniversity.MonarchUniversity.Model.CourseRegistration;
import com.MonarchUniversity.MonarchUniversity.Payload.*;
import com.MonarchUniversity.MonarchUniversity.Repositories.*;
import lombok.AllArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class HodService {
    private final UserRepository userRepository;
    private final LecturerProfileRepo lecturerProfileRepo;
    private final LevelRepository levelRepository;
    private final CourseRepository courseRepository;
    private final DepartmentRepository departmentRepository;
    private final SemesterRepo semesterRepo;
    private final CourseRegistrationRepo courseRegistrationRepo;
    private final SemesterCourseRepo semesterCourseRepo;
    private final StudentProfileRepo studentProfileRepo;
    private final CourseAssessmentRepo assessmentRepo;

    private LecturerProfile getLoggedInLecturerProfile() {
        org.springframework.security.core.userdetails.User springUser =
                (org.springframework.security.core.userdetails.User) SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getPrincipal();

        User userEntity = userRepository.findByUsername(springUser.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return lecturerProfileRepo.findByUser(userEntity)
                .orElseThrow(() -> new RuntimeException("StudentProfile not found"));
    }

    public List<DepartmentDto> getdepartmentsViaHod(){

        LecturerProfile lecturerProfile = getLoggedInLecturerProfile();
        List<Course> courseList = lecturerProfile.getCourses();

        List<Department> departments = new ArrayList<>();
        for(Course course : courseList){
            Department department = course.getDepartment();

            departments.add(department);
        }

        return departments.stream()
                .map(d -> departmentmapToDto(d))
                .collect(Collectors.toList());
    }

    public List<LevelDto> getLevelsViaDepartment(Long departmentId){
        List<Level> levels = levelRepository.findByDepartmentId(departmentId);

        if(levels==null){
            return null;
        }
        return levels.stream()
                .map(l -> levelmapToDto(l)).collect(Collectors.toList());
    }

    public List<CourseResponseDto> getCoursesByLevelAndDepartment(Long levelId, Long departmentId){
        Department department = departmentRepository
                .findById(departmentId).orElseThrow(()-> new ResponseNotFoundException("No such department"));

        Level level = levelRepository.findById(levelId)
                .orElseThrow(()-> new ResponseNotFoundException("No such Level"));

        List<Course> courseList = courseRepository.findAllByDepartmentAndLevel(department,level);
        return courseList.stream()
                .map(course -> coursemapToDto(course))
                .toList();
    }

    public List<SemesterResponseDto> getAllSemester(){
        return semesterRepo.findAll()
                .stream()
                .map(d -> new SemesterResponseDto(
                        d.getId(),
                        d.getSession().getSessionName(),
                        d.getStartDate(),
                        d.getEndDate(),
                        d.getSemesterName()
                )).toList();
    }

    // get courses via lecturer, change update
    public List<CourseResponseDto> getCoursesViaLectuer(){
        LecturerProfile lecturerProfile = getLoggedInLecturerProfile();
        List<Course> courseList = lecturerProfile.getCourses();

        return courseList.stream()
                .map(c -> coursemapToDto(c))
                .collect(Collectors.toList());
    }

    public List<CourseResponseDto> getCoursesViaLecturer() {

        LecturerProfile lecturerProfile = getLoggedInLecturerProfile();
        List<Course> lecturerCourses = lecturerProfile.getCourses(); // your current list

        LocalDate today = LocalDate.now();
        Semester currentSemester = semesterRepo.findAll().stream()
                .filter(s -> !today.isBefore(s.getStartDate()) && !today.isAfter(s.getEndDate()))
                .findFirst()
                .orElseThrow(() -> new ResponseNotFoundException("No active semester found"));

        List<CourseResponseDto> result = new ArrayList<>();

        for (Course course : lecturerCourses) {
            SemesterCourse sc = semesterCourseRepo
                    .findByCourseAndSemester(course, currentSemester)
                    .orElse(null); // skip if not assigned this semester
            if (sc != null) {
                result.add(new CourseResponseDto(

                                sc.getId(),
                                sc.getCourse().getDepartment().getDepartmentName(),
                                sc.getCourse().getLevel().getLevelNumber(),
                                sc.getCourse().getCourseTitle(),
                                sc.getCourse().getCourseType(),
                                sc.getCourse().getCourseCode(),
                                sc.getCourse().getCourseUnit()

                        ));
            }
        }

        return result;
    }


    public List<StudentOfferingCourse> getStudentsOfferingCourse(Long semesterCourseId){

        List<CourseRegistration> registrations =
                courseRegistrationRepo.findBySemesterCourse_Id(semesterCourseId);

        return registrations.stream()
                .map(reg -> reg.getStudentProfile())
                .map(s -> new StudentOfferingCourse(
                        s.getLastName() + " " + s.getFirstName(),
                        s.getMatricNumber(),
                        s.getLevel().getLevelNumber(),
                        s.getDepartment().getDepartmentName()
                ))
                .collect(Collectors.toList());
    }

    public ByteArrayInputStream generateResultTemplate(Long semesterCourseId) throws IOException {

        List<StudentOfferingCourse> students = getStudentsOfferingCourse(semesterCourseId);

        CourseAssessmentStructure assessment = assessmentRepo
                .findBySemesterCourse_Id(semesterCourseId)
                .orElseThrow(() -> new ResponseNotFoundException("Assessment structure not set"));

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Result Sheet");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Matric Number");
        header.createCell(1).setCellValue("Student Name");
        header.createCell(2).setCellValue("Level");
        header.createCell(3).setCellValue("Department");
        header.createCell(4).setCellValue("CA (" + assessment.getMaxCa() + ")");
        header.createCell(5).setCellValue("Exam (" + assessment.getMaxExam() + ")");
        header.createCell(6).setCellValue("Total (" + assessment.getTotal() + ")");

        int rowNum = 1;

        for (StudentOfferingCourse student : students) {

            Row row = sheet.createRow(rowNum);

            row.createCell(0).setCellValue(student.getMatricNumber());
            row.createCell(1).setCellValue(student.getName());
            row.createCell(2).setCellValue(student.getLevelNumber());
            row.createCell(3).setCellValue(student.getDepartmentName());
            row.createCell(4);
            row.createCell(5);

            Cell totalCell = row.createCell(6);

            int excelRow = rowNum + 1;
            totalCell.setCellFormula("E" + excelRow + "+F" + excelRow);

            rowNum++;
        }

        for (int i = 0; i <= 6; i++) {
            sheet.autoSizeColumn(i);
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();

        return new ByteArrayInputStream(out.toByteArray());
    }

    public PagedResponse<StudentOfferingCourse> getStudentsInDeptAndLevel(Long deptId,Long levelId, Integer offset, Integer limit) {

        PageRequest pageReq = new PageRequest(offset, limit, Sort.by(Sort.Direction.DESC, "lastName"));
        LecturerProfile lecturerProfile = getLoggedInLecturerProfile();

        Department department = departmentRepository.findById(deptId)
                .orElseThrow(() -> new ResponseNotFoundException("No such dept"));
        Level level = levelRepository.findById(levelId)
                .orElseThrow(() -> new ResponseNotFoundException("No such level"));

        if (!lecturerProfile.getDepartment().equals(department)) {
            throw new ResponseNotFoundException("Not an Hod or Dean of this dept");
        }


        if (lecturerProfile.getLecturerType() == LecturerProfile.LecturerType.HOD ||
                lecturerProfile.getLecturerType() == LecturerProfile.LecturerType.DEAN) {


        } else if (lecturerProfile.getLecturerType() == LecturerProfile.LecturerType.LEVEL_ADVISER) {

            if (!lecturerProfile.getLevel().getId().equals(levelId)) {
                throw new ResponseNotFoundException("Level adviser cannot access other levels");
            }

        } else {
            throw new ResponseNotFoundException("No such access");
        }
        Page<StudentProfile> studentPage =
                studentProfileRepo.findByDepartmentAndLevel(department, level, pageReq);

        Page<StudentOfferingCourse> mapped =
                studentPage.map(s -> new StudentOfferingCourse(
                        s.getLastName() + " " + s.getFirstName(),
                        s.getMatricNumber(),
                        s.getLevel().getLevelNumber(),
                        s.getDepartment().getDepartmentName()
                ));

        return new PagedResponse<>(mapped);
    }

    public PagedResponse<LecturerDto> getDepartmentalStaffs(Integer offset, Integer limit){

        LecturerProfile lecturerProfile = getLoggedInLecturerProfile();
        Department department = lecturerProfile.getDepartment();

        Set<LecturerProfile> lecturers = new HashSet<>();

        // Level Advisers
        List<LecturerProfile> levelAdvisers =
                lecturerProfileRepo.findByDepartmentAndLecturerType(
                        department,
                        LecturerProfile.LecturerType.LEVEL_ADVISER
                );

        lecturers.addAll(levelAdvisers);

        // Department courses
        List<Course> courses = courseRepository.findByDepartment(department);

        if(!courses.isEmpty()){
            List<LecturerProfile> courseLecturers =
                    lecturerProfileRepo.findByCoursesIn(courses);

            lecturers.addAll(courseLecturers);
        }

        LecturerProfile.LecturerType type = lecturerProfile.getLecturerType();

        if(type == LecturerProfile.LecturerType.HOD){

            // HOD cannot see himself or the dean
            lecturers.removeIf(l ->
                    l.getId().equals(lecturerProfile.getId()) ||
                            l.getLecturerType() == LecturerProfile.LecturerType.DEAN
            );

        } else if(type == LecturerProfile.LecturerType.DEAN){

            // Dean can see everyone except himself
            lecturers.removeIf(l ->
                    l.getId().equals(lecturerProfile.getId())
            );

        } else {

            throw new ResponseNotFoundException("No such access");
        }

        List<LecturerProfile> lecturerList = new ArrayList<>(lecturers);



        int start = offset * limit;
        int end = Math.min(start + limit, lecturerList.size());

        List<LecturerDto> content = lecturerList.subList(start, end)
                .stream()
                .map(l -> new LecturerDto(
                        l.getId(),
                        l.getFullName(),
                        l.getLecturerType() != null ? l.getLecturerType() : null,
                        l.getLevel() != null ? l.getLevel().getId() : null
                ))
                .toList();

        return new PagedResponse<>(
                content,
                content.size(),
                lecturerList.size(),
                end >= lecturerList.size()
        );
    }


    private LevelDto levelmapToDto(Level level) {
        return new LevelDto(
                level.getId(),
                level.getDepartment() != null ? level.getDepartment().getId() : null,
                level.getDepartment().getDepartmentName(),
                level.getLevelNumber(),
                level.getCapacity()
        );
    }

    private DepartmentDto departmentmapToDto(Department saved){
        return new DepartmentDto(
                saved.getId(),
                saved.getDepartmentName(),
                saved.getDepartmentCode(),
                saved.getFaculty().getId(),
                saved.getFaculty().getFacultyName(),
                saved.getDepartmentDescription(),
                saved.getOfficeLocation(),
                saved.getEstablishedYear()
        );
    }

    private CourseResponseDto coursemapToDto(Course course){
        return new CourseResponseDto(course.getId(),
                course.getDepartment().getDepartmentName(),
                course.getLevel().getLevelNumber(),
                course.getCourseTitle(),
                course.getCourseType(),
                course.getCourseCode(),
                course.getCourseUnit()
        );

    }

}

