package com.MonarchUniversity.MonarchUniversity.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.MonarchUniversity.MonarchUniversity.Payload.FacultyDto;
import com.MonarchUniversity.MonarchUniversity.Service.FacultyService;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/super-admin")
public class SuperAdminAccountController {
	private final FacultyService facultyService;
	
	@PostMapping("/faculty")
	public ResponseEntity<?> createFaculty(@RequestBody FacultyDto dto){
		return ResponseEntity.ok(facultyService.createFaculty(dto));
	}
	
	@PutMapping("/faculty/{id}")
	public ResponseEntity<?> editFaculty(@PathVariable Long id, @RequestBody FacultyDto dto){
		return ResponseEntity.ok(facultyService.editFaculty(id, dto));
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteFaculty(@PathVariable Long id){
		return ResponseEntity.ok(facultyService.deleteFaculty(id));
	}
}
