package com.MonarchUniversity.MonarchUniversity.Repositories;

import com.MonarchUniversity.MonarchUniversity.Model.FeeSchedule;
import com.MonarchUniversity.MonarchUniversity.Model.FeeScheduleItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeeScheduleItemRepo extends JpaRepository<FeeScheduleItem, Long> {
    List<FeeScheduleItem> findByFeeSchedule(FeeSchedule feeSchedule);
    List<FeeScheduleItem> findByFeeScheduleOrderByPriorityAsc(FeeSchedule feeSchedule);
}
