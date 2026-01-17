package com.MonarchUniversity.MonarchUniversity.Repositories;

import com.MonarchUniversity.MonarchUniversity.Model.FeeType;
import com.MonarchUniversity.MonarchUniversity.Model.PortalSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PortalScheduleRepo extends JpaRepository<PortalSchedule, Long> {
    boolean existsByFeeType(FeeType feeType);
}
