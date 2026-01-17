package com.MonarchUniversity.MonarchUniversity.Model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Entity
@Data
public class TimetableEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Timetable timetable;

    @Enumerated(EnumType.STRING)
    private DayOfWeek day; // MONDAY, TUESDAY...

    private LocalTime startTime;
    private LocalTime endTime;

    private String courseName;
    private String courseCode;

    private String venue;
}
