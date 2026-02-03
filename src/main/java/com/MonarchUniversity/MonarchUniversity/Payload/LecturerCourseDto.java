package com.MonarchUniversity.MonarchUniversity.Payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class LecturerCourseDto {
    private String courseTitle;
    private String courseCode;
    private Long courseId;
    private Long departmentId;
    private String departmentName;
    private Long levelId;
    private String levelNumber;

}