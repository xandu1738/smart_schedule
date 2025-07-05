package com.servicecops.project.repositories;

import com.servicecops.project.models.database.ShiftAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShiftAssignmentRepository extends JpaRepository<ShiftAssignment, Integer> {
    Optional<ShiftAssignment> findFirstByShiftIdAndEmployeeId(Integer shiftId, Integer employeeId);
}
