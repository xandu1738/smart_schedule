package com.servicecops.project.repositories;

import com.servicecops.project.models.database.*;
import com.servicecops.project.models.jpahelpers.repository.JetRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SystemUserRepository extends JetRepository<SystemUserModel, Long> {
    //    SystemUserModel findFirstByUsername(String username);
    Optional<SystemUserModel> findByEmail(String email);
}

