package com.MonarchUniversity.MonarchUniversity.Repositories;

import com.MonarchUniversity.MonarchUniversity.Model.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface SessionRepo extends JpaRepository<Session, Long> {
    boolean existsBySessionName(String sessionName);

    @Modifying
    @Query("UPDATE Session s SET s.active = false WHERE s.active = true")
    void deactivateAllSessions();

    Optional<Session> findByActiveTrue();
}
