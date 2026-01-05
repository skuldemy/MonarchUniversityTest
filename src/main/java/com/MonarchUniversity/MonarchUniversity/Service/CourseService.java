package com.MonarchUniversity.MonarchUniversity.Service;

import com.MonarchUniversity.MonarchUniversity.Entity.Course;
import com.MonarchUniversity.MonarchUniversity.Entity.Level;
import com.MonarchUniversity.MonarchUniversity.Entity.Program;
import com.MonarchUniversity.MonarchUniversity.Exception.ResponseNotFoundException;
import com.MonarchUniversity.MonarchUniversity.Payload.CourseRequestDto;
import com.MonarchUniversity.MonarchUniversity.Payload.CourseResponseDto;
import com.MonarchUniversity.MonarchUniversity.Repositories.CourseRepository;
import com.MonarchUniversity.MonarchUniversity.Repositories.LevelRepository;
import com.MonarchUniversity.MonarchUniversity.Repositories.ProgramRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class CourseService {

    private final CourseRepository courseRepo;
    private final ProgramRepository programRepository;
    private final LevelRepository levelRepository;

    public CourseResponseDto createCourse(CourseRequestDto dto){

        Program program = programRepository.findById(dto.getProgramId())
                .orElseThrow(() -> new ResponseNotFoundException("No such program"));

        Level level = levelRepository.findById(dto.getLevelId())
                .orElseThrow(() -> new ResponseNotFoundException("No such level"));

        if (!level.getProgram().getId().equals(program.getId())) {
            throw new ResponseNotFoundException("This level is not associated with this program");
        }

        if(courseRepo.existsByCourseCodeIgnoreCaseAndLevelAndProgram(dto.getCourseCode(), level, program)){
            throw new ResponseNotFoundException("Course code already exists for this program and level");
        }

        if(courseRepo.existsByCourseTitleIgnoreCaseAndLevelAndProgram(dto.getCourseTitle(), level, program)){
            throw new ResponseNotFoundException("Course title already exists for this program and level");
        }

        Course course = new Course();
        course.setProgram(program);
        course.setLevel(level);
        course.setCourseCode(dto.getCourseCode());
        course.setCourseTitle(dto.getCourseTitle());
        course.setCourseUnit(dto.getCourseUnit());
        course.setCourseType(dto.getCourseType());

        Course savedCourse = courseRepo.save(course);

        return new CourseResponseDto(course.getId(),
                course.getProgram().getProgramName(),
                course.getLevel().getLevelNumber(),
                course.getCourseTitle(),
                course.getCourseType(),
                course.getCourseCode(),
                course.getCourseUnit()
                );

    }

    public List<CourseResponseDto> getAllCoursesAttachedToProgram(Long programId, Long levelId){

        Program program = programRepository.findById(programId)
                .orElseThrow(() -> new ResponseNotFoundException("No such program"));

        Level level = levelRepository.findById(levelId)
                .orElseThrow(() -> new ResponseNotFoundException("No such level"));


        return courseRepo.findAllByProgramAndLevel(program, level).stream()
                .map(course -> new CourseResponseDto(
                        course.getId(),
                        course.getProgram().getProgramName(),
                        course.getLevel().getLevelNumber(),
                        course.getCourseTitle(),
                        course.getCourseType(),
                        course.getCourseCode(),
                        course.getCourseUnit()
                ))
                .toList();

    }

    @Transactional
    public CourseResponseDto updateCourse(Long courseId, CourseRequestDto dto) {

        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new ResponseNotFoundException("No such course"));

        Program program = programRepository.findById(dto.getProgramId())
                .orElseThrow(() -> new ResponseNotFoundException("No such program"));

        Level level = levelRepository.findById(dto.getLevelId())
                .orElseThrow(() -> new ResponseNotFoundException("No such level"));

        // Validate program ↔ level relationship
        if (!level.getProgram().getId().equals(program.getId())) {
            throw new ResponseNotFoundException("This level is not associated with this program");
        }

        if(courseRepo.existsByCourseCodeIgnoreCaseAndLevelAndProgram(dto.getCourseCode(), level, program)){
            throw new ResponseNotFoundException("Course code already exists for this program and level");
        }

        if(courseRepo.existsByCourseTitleIgnoreCaseAndLevelAndProgram(dto.getCourseTitle(), level, program)){
            throw new ResponseNotFoundException("Course title already exists for this program and level");
        }


        // Update fields
        course.setProgram(program);
        course.setLevel(level);
        course.setCourseCode(dto.getCourseCode());
        course.setCourseTitle(dto.getCourseTitle());
        course.setCourseUnit(dto.getCourseUnit());
        course.setCourseType(dto.getCourseType());

        Course updatedCourse = courseRepo.save(course);

        return new CourseResponseDto(
                updatedCourse.getId(),
                updatedCourse.getProgram().getProgramName(),
                updatedCourse.getLevel().getLevelNumber(),
                updatedCourse.getCourseTitle(),
                updatedCourse.getCourseType(),
                updatedCourse.getCourseCode(),
                updatedCourse.getCourseUnit()
        );
    }

}
