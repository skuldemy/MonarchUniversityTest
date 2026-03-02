package com.MonarchUniversity.MonarchUniversity.Repositories;

import com.MonarchUniversity.MonarchUniversity.Model.LecturerProfile;
import com.MonarchUniversity.MonarchUniversity.Model.Material;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MaterialRepo extends JpaRepository<Material, Long> {
    @Query("""
    SELECT m FROM Material m
    JOIN FETCH m.semesterCourse sc
    JOIN FETCH sc.course c
    JOIN FETCH c.level
    JOIN FETCH c.department
    WHERE m.lecturerProfile = :lecturer
""")
    Page<Material> findByLecturerWithDetails(
            @Param("lecturer") LecturerProfile lecturer,
            Pageable pageable);
}
