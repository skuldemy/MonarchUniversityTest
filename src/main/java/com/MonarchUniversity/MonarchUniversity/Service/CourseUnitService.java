package com.MonarchUniversity.MonarchUniversity.Service;


import com.MonarchUniversity.MonarchUniversity.Model.Level;
import com.MonarchUniversity.MonarchUniversity.Model.Program;
import com.MonarchUniversity.MonarchUniversity.Payload.CourseUnitRequestDto;
import com.MonarchUniversity.MonarchUniversity.Payload.CourseUnitResponseDto;
import com.MonarchUniversity.MonarchUniversity.Payload.CourseUnitUpdate;

import java.util.List;

public interface CourseUnitService {
    public String createCourseUnit(CourseUnitRequestDto dto);
    public CourseUnitResponseDto getCouseUnitResponse(Long programId, Long levelId,
                                                            String semesterName
                                                            );
    public String updateCourseUnit(Long courseUnitId,
                                   CourseUnitUpdate update
    );



}
