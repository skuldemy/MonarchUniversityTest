package com.MonarchUniversity.MonarchUniversity.Service;

import com.MonarchUniversity.MonarchUniversity.Payload.CourseRegistrationResponse;

import java.util.List;

public interface CourseRegService {
    public List<CourseRegistrationResponse> registerCourses(List<Long> courseId);
    public List<CourseRegistrationResponse> getRegisteredCourses();
}
