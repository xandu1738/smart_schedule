package com.servicecops.project.repositories;

import com.servicecops.project.models.database.Employee;
import com.servicecops.project.models.jpahelpers.repository.JetRepository;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JetRepository<Employee, Long> {
    Optional<Employee> findByEmail(String email);
    List<Employee> findByDepartmentAndArchived(Integer department, boolean archived);
    Optional<Employee> findByIdAndArchived(Integer id, boolean archived);
    Optional<List<Employee>> findByDepartmentAndArchivedTrue(Integer department);
    Optional<List<Employee>> findByDepartmentAndArchivedFalse(Integer department);



}
