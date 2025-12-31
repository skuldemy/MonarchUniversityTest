package com.MonarchUniversity.MonarchUniversity.Repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.MonarchUniversity.MonarchUniversity.Entity.StudentProfile;
import com.MonarchUniversity.MonarchUniversity.Entity.User;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long>{
	Optional<User> findByUsername(String username);
	long countByRoles_Name(String roleName);
    @Query("""
    SELECT DISTINCT u
    FROM User u
    JOIN u.roles r
    WHERE r.name NOT IN ('STUDENT', 'SUPER_ADMIN')
""")
    List<User> findAllExceptStudents();

    boolean existsByUsername(String username);


}
