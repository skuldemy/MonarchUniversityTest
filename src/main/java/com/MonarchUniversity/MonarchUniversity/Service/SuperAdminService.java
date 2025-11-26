package com.MonarchUniversity.MonarchUniversity.Service;

import org.springframework.stereotype.Service;

import com.MonarchUniversity.MonarchUniversity.Entity.LecturerProfile;
import com.MonarchUniversity.MonarchUniversity.Entity.User;
import com.MonarchUniversity.MonarchUniversity.Exception.ResponseNotFoundException;
import com.MonarchUniversity.MonarchUniversity.Payload.LecturerRequestDto;
import com.MonarchUniversity.MonarchUniversity.Payload.LecturerResponseDto;
import com.MonarchUniversity.MonarchUniversity.Payload.UserDto;
import com.MonarchUniversity.MonarchUniversity.Repositories.UserRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class SuperAdminService {
	private final UserRepository userRepo;
	
	
//	public LecturerResponseDto createNewUser(LecturerRequestDto dto) {
//		 userRepo.findByUsername(dto.getEmailAddress())
//		        .ifPresent(user -> {
//		            throw new ResponseNotFoundException("User already exists");
//		        });
//		User user = new User();
//		user.setUsername(dto.getEmailAddress());
//		
//		LecturerProfile lecturerProfile = new LecturerProfile();
//		
//	}
}
