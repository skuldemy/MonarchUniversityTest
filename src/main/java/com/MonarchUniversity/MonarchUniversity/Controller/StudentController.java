package com.MonarchUniversity.MonarchUniversity.Controller;

import com.MonarchUniversity.MonarchUniversity.Payload.CourseResponseDto;
import com.MonarchUniversity.MonarchUniversity.Payload.LevelProgramDto;
import com.MonarchUniversity.MonarchUniversity.Payload.PaymentRequestDto;
import com.MonarchUniversity.MonarchUniversity.Payload.StudentPaymentInfoDto;
import com.MonarchUniversity.MonarchUniversity.Impl.CourseService;
import com.MonarchUniversity.MonarchUniversity.Impl.StudentPaymentService;
import com.MonarchUniversity.MonarchUniversity.Service.CourseRegService;
import com.MonarchUniversity.MonarchUniversity.Service.CourseUnitService;
import com.MonarchUniversity.MonarchUniversity.Service.SemesterCourseService;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/student")
public class StudentController {
    private final CourseService courseService;
    private final StudentPaymentService studentPaymentService;
    private final SemesterCourseService semesterCourseService;
    private final CourseUnitService courseUnitService;
    private final CourseRegService courseRegService;

//    new
    @GetMapping("/get-available-amount")
    public ResponseEntity<StudentPaymentInfoDto> getStudentPaymentList(){
        return ResponseEntity.ok(studentPaymentService.getStudentPaymentList());
    }
    // Student makes payment
    @PostMapping("/make-payment")
    public ResponseEntity<StudentPaymentInfoDto> makePayment(@RequestBody PaymentRequestDto dto) {
        return ResponseEntity.ok(studentPaymentService.makePayment(dto));
    }

    @GetMapping(value = "/get-level-and-program")
    public ResponseEntity<LevelProgramDto> getLevelAndProgram(){
        return ResponseEntity.ok(studentPaymentService.getLevelAndProgram());
    }

    // update new
    @GetMapping("/get-available-courses")
    public List<CourseResponseDto> getAllCourses(){
        return semesterCourseService
                .getStudentSemesterCourses();
    }

    @GetMapping("/get-current-units")
    public ResponseEntity<?> getCurrentUnit(){
        return ResponseEntity.ok(courseUnitService.getStudentCourseUnitResponse());
    }

    @PostMapping("/register-courses")
    public ResponseEntity<?> registarCourses(@RequestBody List<Long> courseId){
        return ResponseEntity.ok(courseRegService.registerCourses(courseId));
    }
    @GetMapping("/register-courses")
    public ResponseEntity<?> getCourses(){
        return ResponseEntity.ok(courseRegService.getRegisteredCourses());
    }
}
