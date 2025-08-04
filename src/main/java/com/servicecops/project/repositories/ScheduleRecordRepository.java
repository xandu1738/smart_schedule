package com.servicecops.project.repositories;

import com.servicecops.project.models.database.Schedule;
import com.servicecops.project.models.database.ScheduleRecord;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ScheduleRecordRepository extends JpaRepository<ScheduleRecord, Integer> {


  @Transactional
  @Modifying
  @Query("DELETE FROM ScheduleRecord sr WHERE sr.scheduleId = :scheduleId")
  void deleteByScheduleId(Integer scheduleId);

  List<ScheduleRecord> findAllByEmployeeIdAndScheduleId(Integer employeeId, Integer scheduleId);

  @Query(value = "SELECT DISTINCT sr.employee_id FROM schedule_record sr WHERE sr.schedule_id = :scheduleId", nativeQuery = true)
  List<Long> findDistinctEmployeeIdsForSingleSchedule(@Param("scheduleId") Integer scheduleId);

  @Query(value = """
    SELECT DISTINCT (sr.employee_id)
    FROM schedule_record sr
    WHERE sr.schedule_id = :scheduleId AND sr.shift_id = :shift_id AND sr.employee_id IS NOT NULL
    GROUP BY sr.employee_id
""", nativeQuery = true)
  List<Long> findEmployeesInDistinctShiftsForSingleSchedule(
    @Param("scheduleId") Integer scheduleId,
    @Param("shift_id") Integer shiftId
  );


  @Query(value = """
    SELECT DISTINCT (sr.shift_id)
    FROM schedule_record sr
    WHERE sr.schedule_id = :scheduleId AND sr.shift_id IS NOT NULL
    GROUP BY sr.shift_id
""", nativeQuery = true)
  List<Long> findDistinctShifts(@Param("scheduleId") Integer scheduleId);


//  43 employees
//  3 shifts
//  shift1 has 3 employees
//  shift2 has 20 employees
//  shift3 has 20 employees




  @Query(value = """
    SELECT
        json_build_object(
            'employeeId', sr.employee_id,
            'assignedShiftsCount', SUM(CASE WHEN sr.shift_id IS NOT NULL THEN 1 ELSE 0 END),
            'unassignedRecordsCount', SUM(CASE WHEN sr.shift_id IS NULL THEN 1 ELSE 0 END)
        ) AS employee_shift_summary
    FROM
        schedule_record sr
    WHERE
        sr.schedule_id = :scheduleId
    GROUP BY
        sr.employee_id
    """, nativeQuery = true)
  List<String> findEmployeeShiftSummariesByScheduleId(@Param("scheduleId") Integer scheduleId);







}
