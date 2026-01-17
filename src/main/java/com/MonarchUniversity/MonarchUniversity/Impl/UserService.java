package com.MonarchUniversity.MonarchUniversity.Impl;

import org.springframework.stereotype.Service;

import com.MonarchUniversity.MonarchUniversity.Repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public long getNumberOfAdmins() {
        return userRepository.countByRoles_Name("ADMIN");
    }
    
    public long getNumberOfHods() {
    	return userRepository.countByRoles_Name("HOD");
    }
}
