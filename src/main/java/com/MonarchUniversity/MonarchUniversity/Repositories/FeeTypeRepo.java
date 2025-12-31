package com.MonarchUniversity.MonarchUniversity.Repositories;

import com.MonarchUniversity.MonarchUniversity.Entity.FeeType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FeeTypeRepo extends JpaRepository<FeeType, Long> {
    Optional<FeeType> findByName(String name);
}
