package com.MonarchUniversity.MonarchUniversity.Repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.MonarchUniversity.MonarchUniversity.Entity.LecturerProfile;
import com.MonarchUniversity.MonarchUniversity.Entity.User;

public interface LecturerProfileRepo extends JpaRepository<LecturerProfile, Long> {

	Optional<LecturerProfile> findByUser(User user);
}
