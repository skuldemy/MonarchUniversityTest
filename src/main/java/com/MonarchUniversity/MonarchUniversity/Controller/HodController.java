package com.MonarchUniversity.MonarchUniversity.Controller;

import com.MonarchUniversity.MonarchUniversity.Service.SemesterCourseService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/hod")
public class HodController {
    public final SemesterCourseService semesterCourseService;

    @PostMapping("/semester-course/{levelId}/{departmentId}/{semesterId}")
    public ResponseEntity<?> RegisterCourseToSemester(@PathVariable Long levelId, @PathVariable Long departmentId, @RequestBody List<Long> courseId, @PathVariable Long semesterId){
        return ResponseEntity.ok(semesterCourseService.addCoursesToSemester(levelId,departmentId,courseId,semesterId));
    }
    @GetMapping("/semester-course/{levelId}/{departmentId}/{semesterId}")
    public ResponseEntity<?> getCoursesViaSemester(
          @PathVariable  Long levelId,
            @PathVariable Long departmentId,
            @PathVariable Long semesterId
    ){
        return ResponseEntity.ok(semesterCourseService.getSemesterCourses(levelId,departmentId,semesterId));
    }
}
