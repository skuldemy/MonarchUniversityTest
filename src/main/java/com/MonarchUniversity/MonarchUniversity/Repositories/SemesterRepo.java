package com.MonarchUniversity.MonarchUniversity.Repositories;

import com.MonarchUniversity.MonarchUniversity.Model.Semester;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
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
    @Query("""
    SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END
    FROM Semester s
    WHERE s.session.id = :sessionId
    AND (
        :startDate <= s.endDate
        AND :endDate >= s.startDate
    )
""")
    boolean existsOverlappingSemester(
            @Param("sessionId") Long sessionId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("""
    SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END
    FROM Semester s
    WHERE s.session.id = :sessionId
      AND s.id <> :id
      AND (
            :startDate <= s.endDate
        AND :endDate   >= s.startDate
      )
""")
    boolean existsOverlappingSemesterForUpdate(
            @Param("sessionId") Long sessionId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("id") Long id
    );


}
