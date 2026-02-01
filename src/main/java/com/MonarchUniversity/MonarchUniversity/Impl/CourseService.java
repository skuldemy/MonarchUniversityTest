package com.MonarchUniversity.MonarchUniversity.Impl;

import com.MonarchUniversity.MonarchUniversity.Model.*;
import com.MonarchUniversity.MonarchUniversity.Exception.ResponseNotFoundException;
import com.MonarchUniversity.MonarchUniversity.Payload.CourseRequestDto;
import com.MonarchUniversity.MonarchUniversity.Payload.CourseResponseDto;
import com.MonarchUniversity.MonarchUniversity.Repositories.*;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class CourseService {

    private final CourseRepository courseRepo;
    private final ProgramRepository programRepository;
    private final DepartmentRepository departmentRepository;
    private final LevelRepository levelRepository;
    private final StudentProfileRepo studentProfileRepository;
    private final UserRepository userRepository;

    private StudentProfile getLoggedInStudentProfile() {
        org.springframework.security.core.userdetails.User springUser =
                (org.springframework.security.core.userdetails.User) SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getPrincipal();

        User userEntity = userRepository.findByUsername(springUser.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return studentProfileRepository.findByUser(userEntity)
                .orElseThrow(() -> new RuntimeException("StudentProfile not found"));
    }

    public CourseResponseDto createCourse(CourseRequestDto dto){

        Department department = departmentRepository.findById(dto.getDepartmentId())
                .orElseThrow(() -> new ResponseNotFoundException("No such department"));

        Level level = levelRepository.findById(dto.getLevelId())
                .orElseThrow(() -> new ResponseNotFoundException("No such level"));

        if (!level.getDepartment().getId().equals(department.getId())) {
            throw new ResponseNotFoundException("This level is not associated with this department");
        }

        if(courseRepo.existsByCourseCodeIgnoreCaseAndLevelAndDepartment(dto.getCourseCode(), level, department)){
            throw new ResponseNotFoundException("Course code already exists for this department and level");
        }

        if(courseRepo.existsByCourseTitleIgnoreCaseAndLevelAndDepartment(dto.getCourseTitle(), level, department)){
            throw new ResponseNotFoundException("Course title already exists for this department and level");
        }

        Course course = new Course();
        course.setDepartment(department);
        course.setLevel(level);
        course.setCourseCode(dto.getCourseCode());
        course.setCourseTitle(dto.getCourseTitle());
        course.setCourseUnit(dto.getCourseUnit());
        course.setCourseType(dto.getCourseType());

        Course savedCourse = courseRepo.save(course);

        return new CourseResponseDto(course.getId(),
                course.getDepartment().getDepartmentName(),
                course.getLevel().getLevelNumber(),
                course.getCourseTitle(),
                course.getCourseType(),
                course.getCourseCode(),
                course.getCourseUnit()
                );

    }

    public List<CourseResponseDto> getAllCoursesAttachedToProgram(Long departmentId, Long levelId){

        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new ResponseNotFoundException("No such department"));

        Level level = levelRepository.findById(levelId)
                .orElseThrow(() -> new ResponseNotFoundException("No such level"));


        return courseRepo.findAllByDepartmentAndLevel(department, level).stream()
                .map(course -> mapToDto(course))
                .toList();

    }

    public CourseResponseDto getCourseById(Long id){
        Course course = courseRepo.findById(id)
                .orElseThrow(()-> new ResponseNotFoundException("No such course"));
        return mapToDto(course);
    }

    @Transactional
    public CourseResponseDto updateCourse(Long courseId, CourseRequestDto dto) {

        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new ResponseNotFoundException("No such course"));

        Department department = departmentRepository.findById(dto.getDepartmentId())
                .orElseThrow(() -> new ResponseNotFoundException("No such program"));

        Level level = levelRepository.findById(dto.getLevelId())
                .orElseThrow(() -> new ResponseNotFoundException("No such level"));

        // Validate program ↔ level relationship
        if (!level.getDepartment().getId().equals(department.getId())) {
            throw new ResponseNotFoundException("This level is not associated with this department");
        }

        if(courseRepo.existsByCourseCodeIgnoreCaseAndLevelAndDepartmentAndIdNot(
                dto.getCourseCode(), level, department, courseId)){
            throw new ResponseNotFoundException("Course code already exists for this department and level");
        }

        if(courseRepo.existsByCourseTitleIgnoreCaseAndLevelAndDepartmentAndIdNot(
                dto.getCourseTitle(), level, department, courseId)){
            throw new ResponseNotFoundException("Course title already exists for this department and level");
        }


        // Update fields
        course.setDepartment(department);
        course.setLevel(level);
        course.setCourseCode(dto.getCourseCode());
        course.setCourseTitle(dto.getCourseTitle());
        course.setCourseUnit(dto.getCourseUnit());
        course.setCourseType(dto.getCourseType());

        Course updatedCourse = courseRepo.save(course);

        return mapToDto(course);
    }

    public List<CourseResponseDto> getCoursesAttachedtoProgram(){

        StudentProfile studentProfile = getLoggedInStudentProfile();

        Department department = studentProfile.getDepartment();
        Level level = studentProfile.getLevel();

        return courseRepo.findAllByDepartmentAndLevel(department, level).stream()
                .map(course -> mapToDto(course))
                .toList();

    }

    private CourseResponseDto mapToDto(Course course){
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
