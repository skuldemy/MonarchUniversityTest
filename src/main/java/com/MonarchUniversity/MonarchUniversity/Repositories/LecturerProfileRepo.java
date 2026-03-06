package com.MonarchUniversity.MonarchUniversity.Repositories;

import java.util.List;
import java.util.Optional;

import com.MonarchUniversity.MonarchUniversity.Model.*;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;

public interface LecturerProfileRepo extends JpaRepository<LecturerProfile, Long> {

	Optional<LecturerProfile> findByUser(User user);

    @Query("""
    SELECT lp FROM LecturerProfile lp
    JOIN lp.user u
    JOIN u.roles r
    JOIN lp.courses c
    WHERE r.name = 'HOD'
    AND c.department.id = :deptId
""")
    Optional<LecturerProfile> findHodByDepartment(Long deptId);

    @Query("""
    SELECT COUNT(lp) > 0
    FROM LecturerProfile lp
    JOIN lp.user u
    JOIN u.roles r
    JOIN lp.courses c
    WHERE r.name = 'HOD'
    AND c.department.id = :deptId
""")
    boolean existsHodByDepartment(Long deptId);
    boolean existsByDepartmentAndLecturerType(Department department, LecturerProfile.LecturerType lecturerType);

    boolean existsByDepartmentAndLevelAndLecturerType(Department department,
                                                      Level level,
                                                      LecturerProfile.LecturerType lecturerType);
}
