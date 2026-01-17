package com.MonarchUniversity.MonarchUniversity.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.MonarchUniversity.MonarchUniversity.Model.PortalManagement;


public interface PortalManagementRepo extends JpaRepository<PortalManagement, Long> {
	   boolean existsByPortalNameAndAcademicYearAndSemester(
	            String portalName,
	            String academicYear,
	            String semester
	    );
	   boolean existsByPortalNameAndAcademicYearAndSemesterAndIdNot(
	            String portalName,
	            String academicYear,
	            String semester,
	            Long id
	    );
}
