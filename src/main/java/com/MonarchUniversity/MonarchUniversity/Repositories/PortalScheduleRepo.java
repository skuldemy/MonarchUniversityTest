package com.MonarchUniversity.MonarchUniversity.Repositories;

import com.MonarchUniversity.MonarchUniversity.Entity.FeeType;
import com.MonarchUniversity.MonarchUniversity.Entity.PortalAction;
import com.MonarchUniversity.MonarchUniversity.Entity.PortalSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PortalScheduleRepo extends JpaRepository<PortalSchedule, Long> {
    boolean existsByFeeType(FeeType feeType);
}
