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

    public List<CourseResponseDto> getAllCoursesAttachedToProgram(){
        return null;
    }
}
