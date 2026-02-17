package com.MonarchUniversity.MonarchUniversity.Impl;

import com.MonarchUniversity.MonarchUniversity.Model.*;
import com.MonarchUniversity.MonarchUniversity.Exception.ResponseNotFoundException;
import com.MonarchUniversity.MonarchUniversity.Payload.CourseUnitRequestDto;
import com.MonarchUniversity.MonarchUniversity.Payload.CourseUnitResponseDto;
import com.MonarchUniversity.MonarchUniversity.Payload.CourseUnitUpdate;
import com.MonarchUniversity.MonarchUniversity.Repositories.*;
import com.MonarchUniversity.MonarchUniversity.Service.CourseUnitService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CourseUnitServiceImpl implements CourseUnitService{
    private final LevelRepository levelRepository;
    private final DepartmentRepository departmentRepository;
    private final SemesterRepo semesterRepo;
    private final CourseUnitRepo courseUnitRepo;

    @Override
    public String createCourseUnit(CourseUnitRequestDto dto) {
        Department department = departmentRepository.findById(dto.getDepartmentId())
                .orElseThrow(() -> new ResponseNotFoundException("No such program"));

        Level level = levelRepository.findByIdAndDepartment(dto.getLevelId(), department
                ).orElseThrow(()-> new ResponseNotFoundException("No such level for this program"));

//        if(!level.getSemester().equals(dto.getSemesterName())){
//            throw new ResponseNotFoundException("No such semester for this level," +
//                    " consider updating the semester");
//        }

        if (courseUnitRepo.existsByDepartmentAndLevelAndSemesterName(department,level, dto.getSemesterName())){
            throw new ResponseNotFoundException("This response already exists, you might consider updating");
        }


        CourseUnit courseUnit = new CourseUnit();
        courseUnit.setLevel(level);
        courseUnit.setDepartment(department);
        courseUnit.setSemesterName(dto.getSemesterName());
        courseUnit.setMinUnits(dto.getMinUnits());
        courseUnit.setMaxUnits(dto.getMaxUnits());

        courseUnitRepo.save(courseUnit);
        return "Successfully saved!";
    }

    @Override
    public CourseUnitResponseDto getCourseUnitResponse(Long departmentId,
                                                            Long levelId,
                                                            String semesterName

                                                            ) {

        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(()-> new ResponseNotFoundException("No such available program"));

        Level level = levelRepository.findByIdAndDepartment(levelId,
                department).orElseThrow(()-> new ResponseNotFoundException("No such level for this department"));
        CourseUnit course = courseUnitRepo
                .getCourseUnitByDepartmentAndLevelAndSemesterName(department,level,semesterName);
//        return courseUnitList
//                .stream()
//                .map(this::mapToDto)
//                .collect(Collectors.toList());
   return mapToDto(course);
    }

    @Override
    public String updateCourseUnits(Long courseUnitId, CourseUnitUpdate update) {

    CourseUnit courseUnit = courseUnitRepo.findById(courseUnitId)
            .orElseThrow(()-> new ResponseNotFoundException("No such"));


    courseUnit.setMinUnits(update.getMinUnits());
    courseUnit.setMaxUnits(update.getMaxUnits());

    courseUnitRepo.save(courseUnit);
        return "successfully updated!";
    }

    private CourseUnitResponseDto mapToDto(CourseUnit c) {
        if(c==null) {
            throw new ResponseNotFoundException("No min and max course unit for this semester and level department, pls contact the IT team to update");
        }
        return new CourseUnitResponseDto(
                c.getId() ,
                c.getDepartment().getDepartmentName(),
                c.getLevel().getLevelNumber(),
                c.getSemesterName(),
                c.getMinUnits(),
                c.getMaxUnits()
        );
    }

}
