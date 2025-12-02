package com.MonarchUniversity.MonarchUniversity.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.MonarchUniversity.MonarchUniversity.Payload.StudentProfileRequestDto;
import com.MonarchUniversity.MonarchUniversity.Service.StudentProfileService;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/admin")
public class AdminController {
	private final StudentProfileService studentProfileService;

	@PostMapping("/create-student-profile")
	public ResponseEntity<?> createStudentProfile(@RequestBody StudentProfileRequestDto dto){
		return ResponseEntity.ok(studentProfileService.createStudentProfile(dto));
	}
	@GetMapping("/create-student-profile")
	public ResponseEntity<?> getStudents(){
		return ResponseEntity.ok(studentProfileService.getAllStudents());
	}
	@PutMapping("/create-student-profile/{id}")
	public ResponseEntity<?> updateStudentsViaId(@PathVariable Long id, @RequestBody StudentProfileRequestDto dto){
		return ResponseEntity.ok(studentProfileService.updateStudent(id, dto));
	}
}

