package com.MonarchUniversity.MonarchUniversity.Impl;

import com.MonarchUniversity.MonarchUniversity.Exception.ResponseNotFoundException;
import com.MonarchUniversity.MonarchUniversity.Model.*;
import com.MonarchUniversity.MonarchUniversity.Payload.CourseRegistrationResponse;
import com.MonarchUniversity.MonarchUniversity.Repositories.*;
import com.MonarchUniversity.MonarchUniversity.Service.CourseRegService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
public class CourseRegServiceImpl implements CourseRegService {

    private final UserRepository userRepository;
    private final StudentProfileRepo studentProfileRepo;
    private final SemesterRepo semesterRepo;
    private final SemesterCourseRepo semesterCourseRepo;
    private final CourseUnitRepo courseUnitRepo;
    private final PortalScheduleRepo portalScheduleRepo;
    private final CourseRepository courseRepository;
    private final CourseRegistrationRepo courseRegistrationRepo;

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
    public List<CourseRegistrationResponse> registerCourses(List<Long> courseIds) {

        StudentProfile studentProfile = getLoggedInStudentProfile();
        Level level = studentProfile.getLevel();
        Department department = studentProfile.getDepartment();
        LocalDate today = LocalDate.now();


        Semester currentSemester = semesterRepo.findAll()
                .stream()
                .filter(s -> !today.isBefore(s.getStartDate()) && !today.isAfter(s.getEndDate()))
                .findFirst()
                .orElseThrow(() -> new ResponseNotFoundException(
                        "No academic session found for the current date"
                ));


        if (!portalScheduleRepo
                .existsByFeeType_NameAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                        "Course Registration", today, today
                )) {
            throw new ResponseNotFoundException(
                    "Course registration portal is currently closed. Contact IT for assistance."
            );
        }


        List<SemesterCourse> allSemesterCourses =
                semesterCourseRepo.findBySemesterAndCourse_DepartmentAndCourse_Level(currentSemester,department,level)
                        .stream()
                        .filter(sc ->
                                sc.getCourse().getDepartment().equals(department) &&
                                        sc.getCourse().getLevel().equals(level)
                        )
                        .toList();

        List<SemesterCourse> compulsoryCourses = allSemesterCourses.stream()
                .filter(sc -> {
                    String type = sc.getCourse().getCourseType();
                    return "CORE".equalsIgnoreCase(type) || "REQUIRED".equalsIgnoreCase(type) || "IMPORTANT".equalsIgnoreCase(type);
                })
                .toList();


        Set<Long> selectedIds = new HashSet<>(courseIds);


        List<String> missingCourses = compulsoryCourses.stream()
                .filter(sc -> !selectedIds.contains(sc.getId()))
                .map(sc -> sc.getCourse().getCourseCode())
                .toList();

        if (!missingCourses.isEmpty()) {
            throw new ResponseNotFoundException(
                    "You must register all CORE/REQUIRED courses. Missing: " +
                            String.join(", ", missingCourses)
            );
        }


        if (courseIds.size() != selectedIds.size()) {
            throw new ResponseNotFoundException("Duplicate courses selected");
        }


        List<SemesterCourse> semesterCourses = new ArrayList<>();
        int totalUnits = 0;

        for (Long courseId : courseIds) {
            SemesterCourse semesterCourse = semesterCourseRepo.findById(courseId)
                    .orElseThrow(() -> new ResponseNotFoundException(
                            "One of the courses does not exist"
                    ));


            if (!semesterCourse.getCourse().getDepartment().equals(department) ||
                    !semesterCourse.getCourse().getLevel().equals(level)) {
                throw new ResponseNotFoundException(
                        "Course " + semesterCourse.getCourse().getCourseCode() +
                                " is not in your department or level"
                );
            }

            semesterCourses.add(semesterCourse);
            totalUnits += semesterCourse.getCourse().getCourseUnit();
        }

        CourseUnit deptCourseUnit = courseUnitRepo.getCourseUnitByDepartmentAndLevelAndSemesterName(
                department, level, currentSemester.getSemesterName()
        );

        if (totalUnits < deptCourseUnit.getMinUnits() || totalUnits > deptCourseUnit.getMaxUnits()) {
            throw new ResponseNotFoundException(
                    "Total units (" + totalUnits + ") must be between " +
                            deptCourseUnit.getMinUnits() + " and " +
                            deptCourseUnit.getMaxUnits()
            );
        }

        List<CourseRegistrationResponse> responses = new ArrayList<>();
        for (SemesterCourse sc : semesterCourses) {
            CourseRegistration registration = new CourseRegistration();
            registration.setStudentProfile(studentProfile);
            registration.setSemesterCourse(sc);
            courseRegistrationRepo.save(registration);

            responses.add(new CourseRegistrationResponse(
                    sc.getCourse().getCourseTitle(),
                    sc.getCourse().getCourseUnit(),
                    sc.getCourse().getCourseCode()
            ));
        }

        return responses;
    }

    @Override
    public List<CourseRegistrationResponse> getRegisteredCourses() {

        StudentProfile studentProfile = getLoggedInStudentProfile();
        LocalDate today = LocalDate.now();

        Semester currentSemester = semesterRepo.findAll()
                .stream()
                .filter(s -> !today.isBefore(s.getStartDate())
                        && !today.isAfter(s.getEndDate()))
                .findFirst()
                .orElseThrow(() -> new ResponseNotFoundException(
                        "No academic session found for the current date"
                ));

        List<CourseRegistration> registrations =
                courseRegistrationRepo
                        .findByStudentProfileAndSemesterCourse_Semester(
                                studentProfile,
                                currentSemester
                        );

        return registrations.stream()
                .map(reg -> new CourseRegistrationResponse(
                        reg.getSemesterCourse().getCourse().getCourseTitle(),
                        reg.getSemesterCourse().getCourse().getCourseUnit(),
                        reg.getSemesterCourse().getCourse().getCourseCode()
                ))
                .toList();
    }
}
