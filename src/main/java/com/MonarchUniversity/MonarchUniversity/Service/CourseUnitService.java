package com.MonarchUniversity.MonarchUniversity.Service;


import com.MonarchUniversity.MonarchUniversity.Model.Level;
import com.MonarchUniversity.MonarchUniversity.Model.Program;
import com.MonarchUniversity.MonarchUniversity.Payload.CourseUnitRequestDto;
import com.MonarchUniversity.MonarchUniversity.Payload.CourseUnitResponseDto;
import com.MonarchUniversity.MonarchUniversity.Payload.CourseUnitUpdate;
import com.MonarchUniversity.MonarchUniversity.Payload.StudentCourseUnit;

import java.util.List;

public interface CourseUnitService {
    public String createCourseUnit(CourseUnitRequestDto dto);
    public CourseUnitResponseDto getCourseUnitResponse(Long programId, Long levelId,
                                                            String semesterName
                                                            );

    public StudentCourseUnit getStudentCourseUnitResponse();

    public String updateCourseUnits(Long courseUnitId,
                                   CourseUnitUpdate update
    );



}
