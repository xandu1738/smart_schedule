package com.servicecops.project.repositories;

import com.servicecops.project.models.database.ScheduleRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleRecordRepository extends JpaRepository<ScheduleRecord, Integer> {

}
