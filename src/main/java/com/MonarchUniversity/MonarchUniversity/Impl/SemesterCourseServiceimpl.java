package com.MonarchUniversity.MonarchUniversity.Impl;

import com.MonarchUniversity.MonarchUniversity.Exception.ResponseNotFoundException;
import com.MonarchUniversity.MonarchUniversity.Model.*;
import com.MonarchUniversity.MonarchUniversity.Payload.CourseResponseDto;
import com.MonarchUniversity.MonarchUniversity.Repositories.*;
import com.MonarchUniversity.MonarchUniversity.Service.SemesterCourseService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class SemesterCourseServiceimpl implements SemesterCourseService {
    private final CourseRepository courseRepository;
    private final DepartmentRepository departmentRepository;
    private final LevelRepository levelRepository;
    private final SemesterRepo semesterRepo;
    private final SemesterCourseRepo semesterCourseRepo;
    private final CourseUnitRepo courseUnitRepo;
    private final UserRepository userRepository;
    private final StudentProfileRepo studentProfileRepo;

    private StudentProfile getLoggedInStudentProfile() {
        org.springframework.security.core.userdetails.User springUser =
                (org.springframework.security.core.userdetails.User) SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getPrincipal();

        User userEntity = userRepository.findByUsername(springUser.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return studentProfileRepo.findByUser(userEntity)
                .orElseThrow(() -> new RuntimeException("StudentProfile not found"));
    }


    @Override
    public String addCoursesToSemester(
            Long levelId,
            Long departmentId,
            List<Long> courseIds,
            Long semesterId) {

        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new ResponseNotFoundException("No such department"));

        Level level = levelRepository.findById(levelId)
                .orElseThrow(() -> new ResponseNotFoundException("No such level"));

        Semester semester = semesterRepo.findById(semesterId)
                .orElseThrow(() -> new ResponseNotFoundException("No such semester"));

        String semesterName = semester.getSemesterName();

        CourseUnit deptCourseUnit = courseUnitRepo
                .getCourseUnitByDepartmentAndLevelAndSemesterName(department, level, semesterName);

        List<Course> coursesToAdd = new ArrayList<>();
        int totalUnits = 0;


        for (Long id : courseIds) {

            Course course = courseRepository.findById(id)
                    .orElseThrow(() -> new ResponseNotFoundException("No such course"));

            if (!course.getDepartment().getId().equals(departmentId) ||
                    !course.getLevel().getId().equals(levelId)) {

                throw new ResponseNotFoundException(
                        "Course does not belong to this department and level");
            }

            coursesToAdd.add(course);
            totalUnits += course.getCourseUnit();
        }


        if (totalUnits < deptCourseUnit.getMinUnits() ||
                totalUnits > deptCourseUnit.getMaxUnits()) {

            throw new ResponseNotFoundException(
                    "Total units (" + totalUnits +
                            ") must be between " +
                            deptCourseUnit.getMinUnits() +
                            " and " +
                            deptCourseUnit.getMaxUnits()
            );
        }

        for (Course course : coursesToAdd) {

            if (!semesterCourseRepo.existsByCourseAndSemester(course, semester)) {
                SemesterCourse sc = new SemesterCourse();
                sc.setCourse(course);
                sc.setSemester(semester);
                semesterCourseRepo.save(sc);
            }
        }

        return "Courses successfully added to this semester";
    }

    public List<CourseResponseDto> getSemesterCourses(Long levelId,
                                                      Long departmentId,

                                                      Long semesterId) {

        Level level = levelRepository.findById(levelId)
                .orElseThrow(() -> new ResponseNotFoundException("No such level"));

        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new ResponseNotFoundException("No such department"));


        Semester semester = semesterRepo.findById(semesterId)
                .orElseThrow(() -> new ResponseNotFoundException("No such semester"));

        List<SemesterCourse> semesterCourses =
                semesterCourseRepo.findBySemesterAndCourse_DepartmentAndCourse_Level(
                        semester, department, level
                );

        return semesterCourses.stream()
                .map(sc -> {
                    Course c = sc.getCourse();
                    return new CourseResponseDto(
                            c.getId(),
                            c.getDepartment().getDepartmentName(),
                            c.getLevel().getLevelNumber(),
                            c.getCourseTitle(),
                            c.getCourseType(),
                            c.getCourseCode(),
                            c.getCourseUnit()
                    );
                })
                .toList();
    }

    @Override
    public List<CourseResponseDto> getStudentSemesterCourses() {
        StudentProfile studentProfile = getLoggedInStudentProfile();
        Level level = studentProfile.getLevel();
        LocalDate today = LocalDate.now();
        Semester currentSemester = semesterRepo.findAll()
                .stream()
                .filter(s ->
                        !today.isBefore(s.getStartDate()) &&
                                !today.isAfter(s.getEndDate())
                )
                .findFirst()
                .orElseThrow(() -> new ResponseNotFoundException(
                        "No academic session found for the current date"
                ));

        Department department = studentProfile.getDepartment();

        List<SemesterCourse> semesterCourses =
                semesterCourseRepo.findBySemesterAndCourse_DepartmentAndCourse_Level(
                        currentSemester, department, level
                );

        return semesterCourses.stream()
                .map(sc -> {
                    Course c = sc.getCourse();
                    return new CourseResponseDto(
                            sc.getId(),
                            sc.getCourse().getDepartment().getDepartmentName(),
                            sc.getCourse().getLevel().getLevelNumber(),
                            sc.getCourse().getCourseTitle(),
                            sc.getCourse().getCourseType(),
                            sc.getCourse().getCourseCode(),
                            sc.getCourse().getCourseUnit()
                    );
                })
                .toList();
    }


    @Override
    public int totalSemesterCourse(Long levelId, Long departmentId, Long semesterId) {
        Level level = levelRepository.findById(levelId)
                .orElseThrow(() -> new ResponseNotFoundException("No such level"));

        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new ResponseNotFoundException("No such department"));


        Semester semester = semesterRepo.findById(semesterId)
                .orElseThrow(() -> new ResponseNotFoundException("No such semester"));

        List<SemesterCourse> semesterCourses =
                semesterCourseRepo.findBySemesterAndCourse_DepartmentAndCourse_Level(
                        semester, department, level
                );

        List<Integer> semesterCourseUnits = new ArrayList<>();
        for(SemesterCourse semesterCourse : semesterCourses){
           Integer courseUnit = semesterCourse.getCourse().getCourseUnit();
            semesterCourseUnits.add(courseUnit);
        }
       return semesterCourseUnits.stream().mapToInt(Integer::intValue).sum();

    }

}
