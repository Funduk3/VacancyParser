package com.fedordemin.vacancyparser.repositories;

import com.fedordemin.vacancyparser.entities.VacancyEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface VacancyRepo extends JpaRepository<VacancyEntity, String> {

    @Query("SELECT v FROM VacancyEntity v WHERE " +
            "(COALESCE(:title, '') = '' OR LOWER(v.name) LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
            "(COALESCE(:company, '') = '' OR LOWER(v.employerName) LIKE LOWER(CONCAT('%', :company, '%'))) AND " +
            "(:minSalary IS NULL OR v.salaryFrom >= :minSalary) AND " +
            "(:maxSalary IS NULL OR v.salaryTo <= :maxSalary)")
    Page<VacancyEntity> search(
            @Param("title") String title,
            @Param("company") String company,
            @Param("minSalary") Integer minSalary,
            @Param("maxSalary") Integer maxSalary,
            Pageable pageable
    );
}