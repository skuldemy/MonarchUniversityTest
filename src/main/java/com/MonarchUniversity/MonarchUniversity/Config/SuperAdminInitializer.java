package com.MonarchUniversity.MonarchUniversity.Config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.MonarchUniversity.MonarchUniversity.Entity.Role;
import com.MonarchUniversity.MonarchUniversity.Entity.User;
import com.MonarchUniversity.MonarchUniversity.Repositories.RoleRepository;
import com.MonarchUniversity.MonarchUniversity.Repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SuperAdminInitializer implements CommandLineRunner {
	
	private final UserRepository userRepo;
	private final RoleRepository roleRepo;
	private final PasswordEncoder passwordEncoder;
	
	@Override
	public void run(String... args) throws Exception {
		// TODO Auto-generated method stub
		
		if (userRepo.findByUsername("ITTeam@monarchuniversity.edu.ng").isEmpty()) {

		    Role superAdminRole = roleRepo.findByName("SUPER_ADMIN")
		            .orElseThrow(() -> new RuntimeException("SUPER_ADMIN role not found!"));

		    User superAdmin = new User();
		    superAdmin.setUsername("ITTeam@monarchuniversity.edu.ng");
		    superAdmin.setPassword(passwordEncoder.encode("ITTeam@MU!7294"));
		    superAdmin.getRoles().add(superAdminRole);

		    userRepo.save(superAdmin);
		    System.out.println("SUPER ADMIN created.");
		}

	}
	

}
