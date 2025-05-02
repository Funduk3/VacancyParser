package com.fedordemin.vacancyparser.repositories;

import com.fedordemin.vacancyparser.models.entities.EmployerEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyRepo extends CrudRepository<EmployerEntity, Long> {

}
