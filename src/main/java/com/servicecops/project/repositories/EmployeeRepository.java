package com.servicecops.project.repositories;

import com.servicecops.project.models.database.Employee;
import com.servicecops.project.models.jpahelpers.repository.JetRepository;

import java.util.Optional;

public interface EmployeeRepository extends JetRepository<Employee, Long> {
    Optional<Employee> findByEmail(String email);
    
}
