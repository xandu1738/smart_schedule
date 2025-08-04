package com.servicecops.project.repositories;

import com.servicecops.project.models.database.Employee;
import com.servicecops.project.models.jpahelpers.repository.JetRepository;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JetRepository<Employee, Long> {
    Optional<Employee> findByEmail(String email);
    List<Employee> findAllByDepartmentAndArchived(Integer department, boolean archived);
    List<Employee> findAllByDepartment(Integer department);
    Optional<Employee> findByIdAndArchived(Integer id, boolean archived);

    List<Employee> findAllByDepartmentAndArchivedTrue(Integer department);

  List<Employee> findAllByDepartmentAndArchivedFalse(Integer department);
}
