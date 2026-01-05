package com.MonarchUniversity.MonarchUniversity.Payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseResponseDto {
    private Long id;
    private String programName;
    private String levelName;
    private String courseTitle;
    private String courseType; // elective, core, specialization
    private String courseCode;
    private Integer courseUnit; // 1,2,3,4
}
