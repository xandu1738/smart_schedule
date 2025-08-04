package com.servicecops.project.repositories;

import com.servicecops.project.models.database.Department;
import com.servicecops.project.models.database.Institution;
import com.servicecops.project.models.jpahelpers.repository.JetRepository;
import org.springframework.data.domain.Limit;

import java.util.List;
import java.util.Optional;

public interface DepartmentRepository extends JetRepository<Department, Long> {


  Optional<Department> findByName(String name);
  Optional<Department> findByNameAndInstitutionId(String departmentName, Long institutionId);

  boolean existsByNameAndInstitutionId(String name, Long institutionId);


  Optional<List<Department>> findByInstitutionId(Long institutionId);

}
