package com.MonarchUniversity.MonarchUniversity.Impl;

import com.MonarchUniversity.MonarchUniversity.Exception.ResponseNotFoundException;
import com.MonarchUniversity.MonarchUniversity.Model.*;
import com.MonarchUniversity.MonarchUniversity.Payload.CourseResponseDto;
import com.MonarchUniversity.MonarchUniversity.Payload.DepartmentDto;
import com.MonarchUniversity.MonarchUniversity.Payload.LevelDto;
import com.MonarchUniversity.MonarchUniversity.Payload.SemesterResponseDto;
import com.MonarchUniversity.MonarchUniversity.Repositories.*;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
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

