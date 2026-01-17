package com.MonarchUniversity.MonarchUniversity.Impl;

import com.MonarchUniversity.MonarchUniversity.Model.CourseUnit;
import com.MonarchUniversity.MonarchUniversity.Model.Level;
import com.MonarchUniversity.MonarchUniversity.Model.Program;
import com.MonarchUniversity.MonarchUniversity.Exception.ResponseNotFoundException;
import com.MonarchUniversity.MonarchUniversity.Payload.CourseUnitRequestDto;
import com.MonarchUniversity.MonarchUniversity.Payload.CourseUnitResponseDto;
import com.MonarchUniversity.MonarchUniversity.Repositories.CourseUnitRepo;
import com.MonarchUniversity.MonarchUniversity.Repositories.LevelRepository;
import com.MonarchUniversity.MonarchUniversity.Repositories.ProgramRepository;
import com.MonarchUniversity.MonarchUniversity.Repositories.SemesterRepo;
import com.MonarchUniversity.MonarchUniversity.Service.CourseUnitService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CourseUnitServiceImpl implements CourseUnitService{
    private final LevelRepository levelRepository;
    private final ProgramRepository programRepository;
    private final SemesterRepo semesterRepo;
    private final CourseUnitRepo courseUnitRepo;

    @Override
    public String createCourseUnit(CourseUnitRequestDto dto) {
        Program program = programRepository.findById(dto.getProgramId())
                .orElseThrow(()-> new ResponseNotFoundException("No such available program"));

        Level level = levelRepository.findByIdAndProgram(dto.getLevelId(),
                program).orElseThrow(()-> new ResponseNotFoundException("No such level for this program"));

        if(!level.getSemester().equals(dto.getSemesterName())){
            throw new ResponseNotFoundException("No such semester for this level," +
                    " consider updating the semester");
        }

        CourseUnit courseUnit = new CourseUnit();
        courseUnit.setLevel(level);
        courseUnit.setProgram(program);
        courseUnit.setSemesterName(dto.getSemesterName());
        courseUnit.setMinUnits(dto.getMinUnits());
        courseUnit.setMaxUnits(dto.getMaxUnits());

        courseUnitRepo.save(courseUnit);
        return "Successfully saved!";
    }

    @Override
    public List<CourseUnitResponseDto> getCouseUnitResponse(Long programId,
                                                            Long levelId,
                                                            String semesterName

                                                            ) {

        Program program = programRepository.findById(programId)
                .orElseThrow(()-> new ResponseNotFoundException("No such available program"));

        Level level = levelRepository.findByIdAndProgram(levelId,
                program).orElseThrow(()-> new ResponseNotFoundException("No such level for this program"));

        if(!level.getSemester().equals(semesterName)){
            throw new ResponseNotFoundException("No such semester for this level," +
                    " consider updating the semester");
        }


        List<CourseUnit> courseUnitList = courseUnitRepo
                .getCourseUnitByProgramAndLevelAndSemesterName(program,level,semesterName);
        return courseUnitList
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private CourseUnitResponseDto mapToDto(CourseUnit c) {
        return new CourseUnitResponseDto(
                c.getProgram().getProgramName(),
                c.getLevel().getLevelNumber(),
                c.getSemesterName(),
                c.getMinUnits(),
                c.getMaxUnits()
        );
    }

}
