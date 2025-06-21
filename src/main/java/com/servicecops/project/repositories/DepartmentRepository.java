package com.servicecops.project.repositories;

import com.servicecops.project.models.database.Department;
import com.servicecops.project.models.jpahelpers.repository.JetRepository;
import org.springframework.data.domain.Limit;

import java.util.List;

public interface DepartmentRepository extends JetRepository<Department, Long> {


  Department findByName(String name);

  boolean existsByName(String name);

  List<Department> findByInstitutionId(Long institutionId);

}
