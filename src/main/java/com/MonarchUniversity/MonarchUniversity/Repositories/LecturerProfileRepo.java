package com.MonarchUniversity.MonarchUniversity.Repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.MonarchUniversity.MonarchUniversity.Model.LecturerProfile;
import com.MonarchUniversity.MonarchUniversity.Model.User;

public interface LecturerProfileRepo extends JpaRepository<LecturerProfile, Long> {

	Optional<LecturerProfile> findByUser(User user);
}
