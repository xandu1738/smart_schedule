package com.servicecops.project.repositories;

import com.servicecops.project.models.database.Shift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface ShiftRepository extends JpaRepository<Shift, Integer> {
    List<Shift> findAllByDepartmentId(Integer departmentId);

    @Query(value = """
            WITH shift_data AS (
                SELECT
                    s.id,
                    d.name AS department_name,
                    s.type,
                    s.name,
                    s.start_time,
                    s.end_time,
                    s.created_at,
                    s.created_by,
                    s.max_people
                FROM shift s
                         LEFT JOIN departments d ON d.id = s.department_id
                WHERE s.id = :shiftId
            ),
                 schedule_record_data AS (
                     SELECT
                         sr.shift_id,
                         json_build_object(
                                 'id', sr.id,
                                 'employee_id', sr.employee_id,
                                 'employee_name', e.name,
                                 'start_time', sr.start_time,
                                 'end_time', sr.end_time
                         ) AS schedule_entry
                     FROM schedule_record sr
                              LEFT JOIN employee e ON e.id = sr.employee_id
                     WHERE sr.shift_id = :shiftId
                 )
            SELECT
                sd.*,
                COALESCE(json_agg(srd.schedule_entry) FILTER (WHERE srd.schedule_entry IS NOT NULL), '[]') AS schedule_records
            FROM shift_data sd
                     LEFT JOIN schedule_record_data srd ON sd.id = srd.shift_id
            GROUP BY sd.id, sd.department_name, sd.type, sd.name, sd.start_time, sd.end_time, sd.created_at, sd.created_by, sd.max_people
            """, nativeQuery = true)
    Optional<Map<String, Object>> getShiftDetails(Long employee);
}
