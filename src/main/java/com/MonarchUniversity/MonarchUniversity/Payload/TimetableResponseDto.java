package com.MonarchUniversity.MonarchUniversity.Payload;

import com.MonarchUniversity.MonarchUniversity.Model.Timetable;
import lombok.Data;

import java.util.List;

@Data
public class TimetableResponseDto {
    private Long timetableId;
    private String faculty;
    private String department;
    private String program;
    private String level;
    private String semester;
    private Integer academicYear;
    private String status;
    private List<TimetableEntryDto> entries;

    public static TimetableResponseDto from(
            Timetable timetable,
            List<TimetableEntryDto> entries
    ) {
        TimetableResponseDto dto = new TimetableResponseDto();
        dto.setTimetableId(timetable.getId());
        dto.setFaculty(timetable.getFaculty().getFacultyName());
        dto.setDepartment(timetable.getDepartment().getDepartmentName());
        dto.setProgram(timetable.getDepartment().getDepartmentName());
        dto.setLevel(timetable.getLevel().getLevelNumber());
        dto.setSemester(timetable.getSemester());
        dto.setAcademicYear(timetable.getAcademicYear());
        dto.setStatus(timetable.getStatus().name());
        dto.setEntries(entries);
        return dto;
    }
}
