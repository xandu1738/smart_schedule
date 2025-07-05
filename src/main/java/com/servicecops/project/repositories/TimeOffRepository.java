package com.servicecops.project.repositories;

import com.servicecops.project.models.database.TimeOffRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface TimeOffRepository extends JpaRepository<TimeOffRequest, Integer> {
    List<TimeOffRequest> findAllByEmployeeId(Integer employeeId);

    @Query(value = """
            select tor.id,
                   e.name as employee_name,
                   su.first_name || ' ' || su.last_name as requested_by_name,
                   sa.first_name || ' ' || sa.last_name as approved_by_name,
                   tor.start_date,
                   tor.end_date,
                    tor.requested_by,
                    tor.approved_by,
                   tor.status,
                   tor.requested_on,
                   tor.approved_on,
                   tor.reason
            from time_off_requests tor
                     left join public.employee e on e.id = tor.employee_id
                     left join public.system_user su on su.id = tor.requested_by
                     left join public.system_user sa on sa.id = tor.approved_by
            """, nativeQuery = true)
    List<Map<String,Object>> getTimeOffRequests();
    @Query(value = """
            select tor.id,
                   e.name as employee_name,
                   su.first_name || ' ' || su.last_name as requested_by_name,
                   sa.first_name || ' ' || sa.last_name as approved_by_name,
                   tor.start_date,
                   tor.end_date,
                   tor.status,
                    tor.requested_by,
                    tor.approved_by,
                   tor.requested_on,
                   tor.approved_on,
                   tor.reason
            from time_off_requests tor
                     left join public.employee e on e.id = tor.employee_id
                     left join public.system_user su on su.id = tor.requested_by
                     left join public.system_user sa on sa.id = tor.approved_by
            where tor.employee_id = :employeeId
            """, nativeQuery = true)
    List<Map<String,Object>> getEmployeeTimeOffRequests(Integer employeeId);
}
