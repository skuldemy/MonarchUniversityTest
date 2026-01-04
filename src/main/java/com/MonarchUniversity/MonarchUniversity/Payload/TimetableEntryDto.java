package com.MonarchUniversity.MonarchUniversity.Payload;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class TimetableEntryDto {
    private String day;
    private String startTime;
    private String endTime;
    private String courseCode;
    private String courseTitle;
    private String venue;
}

