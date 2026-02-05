package com.MonarchUniversity.MonarchUniversity.Repositories;

import com.MonarchUniversity.MonarchUniversity.Model.Semester;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface SemesterRepo extends JpaRepository<Semester, Long> {
    @Modifying
    @Query("""
        UPDATE Semester s
        SET s.active = false
        WHERE s.session.id = :sessionId
          AND s.active = true
    """)
    void deactivateAllBySessionId(Long sessionId);

    Optional<Semester> findBySessionIdAndActiveTrue(Long sessionId);

    boolean existsBySessionIdAndSemesterName(Long sessionId, String semesterName);
    boolean existsBySessionIdAndSemesterNameAndIdNot(
            Long sessionId,
            String semesterName,
            Long id
    );
}
