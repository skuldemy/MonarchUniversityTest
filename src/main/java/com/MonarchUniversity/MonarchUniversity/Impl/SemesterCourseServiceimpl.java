package com.MonarchUniversity.MonarchUniversity.Impl;

import com.MonarchUniversity.MonarchUniversity.Exception.ResponseNotFoundException;
import com.MonarchUniversity.MonarchUniversity.Model.*;
import com.MonarchUniversity.MonarchUniversity.Payload.CourseResponseDto;
import com.MonarchUniversity.MonarchUniversity.Repositories.*;
import com.MonarchUniversity.MonarchUniversity.Service.SemesterCourseService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class SemesterCourseServiceimpl implements SemesterCourseService {
    private final CourseRepository courseRepository;
    private final DepartmentRepository departmentRepository;
    private final LevelRepository levelRepository;
    private final SemesterRepo semesterRepo;
    private final SemesterCourseRepo semesterCourseRepo;

    @Override
    public String addCoursesToSemester(Long levelId, Long departmentId, List<Long> courseIds, Long semesterId) {
        Department department =  departmentRepository.findById(departmentId)
                .orElseThrow(()-> new ResponseNotFoundException("No such department"));
        Level level =  levelRepository.findById(levelId)
                .orElseThrow(()-> new ResponseNotFoundException("No such department"));

        Semester semester = semesterRepo.findById(semesterId)
                .orElseThrow(() -> new ResponseNotFoundException("No such semester"));

        for(Long id : courseIds){
            Course course = courseRepository.findById(id)
                    .orElseThrow(()-> new ResponseNotFoundException("No such course"));

            if (!course.getDepartment().getId().equals(departmentId) ||
                    !course.getLevel().getId().equals(levelId)) {
                throw new ResponseNotFoundException(
                        "Course does not belong to this department and level");
            }
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

}
