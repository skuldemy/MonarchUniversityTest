package com.MonarchUniversity.MonarchUniversity.Controller;

import com.MonarchUniversity.MonarchUniversity.Impl.HodService;
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
    private final HodService hodService;

    @GetMapping("/get-departments-of-hod")
    public ResponseEntity<?> getDepartmentsByHod(){
        return ResponseEntity.ok(hodService.getdepartmentsViaHod());
    }

    @GetMapping("/get-levels-via-depeartment/{departmentId}")
    public ResponseEntity<?> getLevelsViaDepartmentId(@PathVariable Long departmentId){
        return ResponseEntity.ok(hodService.getLevelsViaDepartment(departmentId));
    }


    @GetMapping("/get-courses-via-levels-department/{levelId}/{departmentId}")
    public ResponseEntity<?> getCoursesByLevelAndDepartment(@PathVariable Long levelId, @PathVariable Long departmentId){
        return ResponseEntity.ok(hodService.getCoursesByLevelAndDepartment(levelId,departmentId));
    }

    @GetMapping("/semesters")
    public ResponseEntity<?> getAllSemesters(){
        return ResponseEntity.ok(hodService.getAllSemester());
    }

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
