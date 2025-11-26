package com.MonarchUniversity.MonarchUniversity.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.MonarchUniversity.MonarchUniversity.Entity.LecturerProfile;

public interface LecturerProfileRepo extends JpaRepository<LecturerProfile, Long> {

}
