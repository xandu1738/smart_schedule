package com.servicecops.project.repositories;

import com.servicecops.project.models.database.Institution;
import com.servicecops.project.models.jpahelpers.repository.JetRepository;

import java.util.Optional;

public interface InstitutionRepository extends JetRepository<Institution, Long> {

  Optional<Institution> findByCode(String code);

  Optional<Institution> findByName(String name);

  Optional<Institution> findByRegNo(String regNo);

}
