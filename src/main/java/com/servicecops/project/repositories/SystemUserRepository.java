package com.servicecops.project.repositories;

import com.servicecops.project.models.database.*;
import com.servicecops.project.models.jpahelpers.repository.JetRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface SystemUserRepository extends JetRepository<SystemUserModel, Long> {
    //    SystemUserModel findFirstByUsername(String username);
    Optional<SystemUserModel> findByEmail(String email);

    @Query(value = """
            select sr.id,
                   sr.first_name,
                   sr.last_name,
                   sr.email,
                   replace(sr.role_code,'_', ' ') as role,
                   coalesce(sr.username, sr.email) as username,
                   sr.created_at,
                   sr.last_logged_in_at,
                   sr.is_active,
                   i.name as institution_name
            from "system_user" sr
                     left join institution i on i.id = sr.institution_id
                        order by sr.created_at desc
            """, nativeQuery = true
    )
    List<Map<String, Object>> findAllUsers();

    @Query(value = """
            select sr.id,
                   sr.first_name,
                   sr.last_name,
                   sr.email,
                   replace(sr.role_code,'_', ' ') as role,
                   coalesce(sr.username, sr.email) as username,
                   sr.created_at,
                   sr.last_logged_in_at,
                   sr.is_active,
                   i.name as institution_name
            from "system_user" sr
                     left join institution i on i.id = sr.institution_id
                        where sr.id = :userId
            """, nativeQuery = true
    )
    Optional<Map<String, Object>> findByUserId(Long userId);

    @Query(value = """
            select sr.id,
                   sr.first_name,
                   sr.last_name,
                   sr.email,
                   replace(sr.role_code,'_', ' ') as role,
                   coalesce(sr.username, sr.email) as username,
                   sr.created_at,
                   sr.last_logged_in_at,
                   sr.is_active,
                   i.name as institution_name
            from "system_user" sr
                     left join institution i on i.id = sr.institution_id
                        where sr.email = :userEmail
            """, nativeQuery = true
    )
    Optional<Map<String, Object>> findByUserEmail(String userEmail);
}

