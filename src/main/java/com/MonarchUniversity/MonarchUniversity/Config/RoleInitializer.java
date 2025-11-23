package com.MonarchUniversity.MonarchUniversity.Config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.MonarchUniversity.MonarchUniversity.Entity.Role;
import com.MonarchUniversity.MonarchUniversity.Repositories.RoleRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RoleInitializer implements CommandLineRunner {

	private final RoleRepository roleRepo;
	
	@Override
	public void run(String... args) throws Exception {
		// TODO Auto-generated method stub
		 	createRoleIfNotExists("SUPER_ADMIN");
	        createRoleIfNotExists("ADMIN");
	        createRoleIfNotExists("FACULTY_HEAD");
	        createRoleIfNotExists("LECTURER");
	        createRoleIfNotExists("STUDENT");
	        createRoleIfNotExists("HOD");
	        createRoleIfNotExists("LEVEL_ADVISER");
	}
	
	
//	Admin can create anybody
	
	 private void createRoleIfNotExists(String roleName) {
	        if (roleRepo.findByName(roleName).isEmpty()) {
	            roleRepo.save(new Role(null, roleName));
	            System.out.println("Role created: " + roleName);
	        }
	    }

}
