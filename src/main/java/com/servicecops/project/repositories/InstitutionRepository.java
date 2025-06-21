package com.servicecops.project.repositories;

import com.servicecops.project.models.database.Institution;
import com.servicecops.project.models.jpahelpers.repository.JetRepository;

public interface InstitutionRepository extends JetRepository<Institution, Long> {


    Institution findByCode(String code);

    Institution findByName(String name);

    boolean existsByCode(String code);

    boolean existsByName(String name);

    void deleteByCode(String code);

    void deleteByName(String name);



}
