package com.MonarchUniversity.MonarchUniversity.Repositories;

import java.util.List;
import java.util.Optional;

import com.MonarchUniversity.MonarchUniversity.Model.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import com.MonarchUniversity.MonarchUniversity.Model.LecturerProfile;
import com.MonarchUniversity.MonarchUniversity.Model.User;

public interface LecturerProfileRepo extends JpaRepository<LecturerProfile, Long> {

	Optional<LecturerProfile> findByUser(User user);
}
