package com.servicecops.project.repositories;

import com.servicecops.project.models.database.SystemRolePermissionAssignmentModel;
import com.servicecops.project.models.jpahelpers.repository.JetRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface SystemRolePermissionRepository extends JetRepository<SystemRolePermissionAssignmentModel, Long> {
    Optional<SystemRolePermissionAssignmentModel> findByRoleCodeAndPermissionCode(String roleCode, String permissionCode);

    List<SystemRolePermissionAssignmentModel> findAllByRoleCode(String roleCode);

    @Query(value = """
            select srpa.permission_code,
                   replace(srpa.permission_code,'_', ' ') as permission_name
            from system_role_permission_assignment srpa
                     left join system_permission sp on sp.permission_code = srpa.permission_code
            where srpa.role_code = :roleCode
            """, nativeQuery = true)
    List<Map<String,Object>> findPermissionsByRoleCode(String roleCode);
}
