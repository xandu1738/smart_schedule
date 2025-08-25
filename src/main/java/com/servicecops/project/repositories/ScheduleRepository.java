package com.servicecops.project.repositories;

import com.servicecops.project.models.database.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ScheduleRepository extends JpaRepository<Schedule, Integer>  {

  Optional<Schedule> findById(int id);
  Optional<Schedule> findAllByDepartmentId(int id);
  Optional<List<Schedule>> findAllByInstitutionId(Integer institutionId);

  List<Schedule> findAllByInstitutionIdAndDepartmentId(int institutionId, int departmentId);

}
