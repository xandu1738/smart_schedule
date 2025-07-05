package com.servicecops.project.repositories;

import com.servicecops.project.models.database.TimeOffRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TimeOffRepository extends JpaRepository<TimeOffRequest,Integer> {
}
