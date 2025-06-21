package com.servicecops.project.repositories;

import com.servicecops.project.models.database.ShiftSwapRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShiftSwapRepository extends JpaRepository<ShiftSwapRequest,Integer> {
}
