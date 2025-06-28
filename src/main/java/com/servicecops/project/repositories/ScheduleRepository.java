package com.servicecops.project.repositories;

import com.servicecops.project.models.database.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ScheduleRepository extends JpaRepository<Schedule, Integer>  {

  Optional<Schedule> findById(int id);

}
