package com.servicecops.project.repositories;

import com.servicecops.project.models.database.ScheduleRecord;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ScheduleRecordRepository extends JpaRepository<ScheduleRecord, Integer> {


    @Transactional
    @Modifying
    @Query("DELETE FROM ScheduleRecord sr WHERE sr.scheduleId = :scheduleId")
    void deleteByScheduleId(Integer scheduleId);

    List<ScheduleRecord> findAllByEmployeeIdAndScheduleId(Integer employeeId, Integer scheduleId);
}
