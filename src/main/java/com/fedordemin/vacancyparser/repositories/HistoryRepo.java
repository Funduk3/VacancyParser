package com.fedordemin.vacancyparser.repositories;

import com.fedordemin.vacancyparser.entities.LogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HistoryRepo extends JpaRepository<LogEntity, String> {
}
