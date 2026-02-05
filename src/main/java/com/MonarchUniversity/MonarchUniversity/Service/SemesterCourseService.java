package com.MonarchUniversity.MonarchUniversity.Service;

import com.MonarchUniversity.MonarchUniversity.Payload.CourseResponseDto;

import java.util.List;

public interface SemesterCourseService {
    public String addCoursesToSemester(Long levelId, Long departmentId, List<Long> couseId, Long semesterId);
    public List<CourseResponseDto> getSemesterCourses(Long levelId,
                                                      Long departmentId,

                                                      Long semesterId);
}
