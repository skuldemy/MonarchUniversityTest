package com.MonarchUniversity.MonarchUniversity.Controller;

import com.MonarchUniversity.MonarchUniversity.Payload.CourseResponseDto;
import com.MonarchUniversity.MonarchUniversity.Payload.LevelProgramDto;
import com.MonarchUniversity.MonarchUniversity.Payload.PaymentRequestDto;
import com.MonarchUniversity.MonarchUniversity.Payload.StudentPaymentInfoDto;
import com.MonarchUniversity.MonarchUniversity.Impl.CourseService;
import com.MonarchUniversity.MonarchUniversity.Impl.StudentPaymentService;
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
    @GetMapping("/get-available-courses")
    public List<CourseResponseDto> getAllCourses(){
        return courseService.getCoursesAttachedtoProgram();
    }

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

}
