package com.servicecops.project.repositories;

import com.servicecops.project.models.database.ShiftSwapRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface ShiftSwapRepository extends JpaRepository<ShiftSwapRequest,Integer> {
    @Query(value = """
            select ssr.id,
                   e.name  as from_employee_name,
                   e2.name as to_employee_name,
                   ssr.from_employee,
                   ssr.to_employee,
                   ssr.shift_id,
                   ssr.status,
                   su.first_name || ' ' || su.last_name as requested_by_user,
                    sa.first_name || ' ' || sa.last_name as approved_by_user,
                   ssr.approved_by,
                   ssr.requested_by,
                   ssr.approved_on,
                   ssr.requested_on,
                   ssr.updated_by
            from shift_swap_request ssr
                     left join public.employee e on e.id = ssr.from_employee
                     left join public.employee e2 on e2.id = ssr.to_employee
            left join public.system_user su on su.id = ssr.requested_by
            left join public.system_user sa on sa.id = ssr.approved_by
            where ssr.from_employee = :employeeId
            """, nativeQuery = true)
    List<Map<String, Object>> getEmployeeSwapRequests(Integer employeeId);
    @Query(value = """
            select ssr.id,
                   e.name  as from_employee_name,
                   e2.name as to_employee_name,
                   ssr.from_employee,
                   ssr.to_employee,
                   ssr.shift_id,
                   ssr.status,
                   su.first_name || ' ' || su.last_name as requested_by_user,
                    sa.first_name || ' ' || sa.last_name as approved_by_user,
                   ssr.approved_by,
                   ssr.requested_by,
                   ssr.approved_on,
                   ssr.requested_on,
                   ssr.updated_by
            from shift_swap_request ssr
                     left join public.employee e on e.id = ssr.from_employee
                     left join public.employee e2 on e2.id = ssr.to_employee
            left join public.system_user su on su.id = ssr.requested_by
            left join public.system_user sa on sa.id = ssr.approved_by
            where ssr.id = :id
            """, nativeQuery = true)
    Optional<Map<String, Object>> getEmployeeSwapRequestById(Integer id);

    @Query(value = """
            select ssr.id,
                   e.name  as from_employee_name,
                   e2.name as to_employee_name,
                   ssr.from_employee,
                   ssr.to_employee,
                   ssr.shift_id,
                   ssr.status,
                   su.first_name || ' ' || su.last_name as requested_by_user,
                     sa.first_name || ' ' || sa.last_name as approved_by_user,
                   ssr.approved_by,
                   ssr.requested_by,
                   ssr.approved_on,
                   ssr.requested_on,
                   ssr.updated_by
            from shift_swap_request ssr
                     left join public.employee e on e.id = ssr.from_employee
                     left join public.employee e2 on e2.id = ssr.to_employee
            left join public.system_user su on su.id = ssr.requested_by
            left join public.system_user sa on sa.id = ssr.approved_by
            """, nativeQuery = true)
    List<Map<String, Object>> getAllSwapRequests();
}
