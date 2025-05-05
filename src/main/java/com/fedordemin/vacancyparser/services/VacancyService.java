package com.fedordemin.vacancyparser.services;

import com.fedordemin.vacancyparser.models.entities.*;
import com.fedordemin.vacancyparser.models.Vacancy;
import com.fedordemin.vacancyparser.repositories.VacancyRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class VacancyService {
    private final VacancyRepo repository;

    @Autowired
    public VacancyService(VacancyRepo repository) {
        this.repository = repository;
    }

    @Value("${app.pagination.default-size:10}")
    private int defaultPageSize;

    public Page<VacancyEntity> getVacancies(
            String title,
            String company,
            Integer minSalary,
            Integer maxSalary,
            int page,
            int size
    ) {
        int pageSize = size > 0 ? size : defaultPageSize;
        return repository.search(
                title,
                company,
                minSalary,
                maxSalary,
                PageRequest.of(page, pageSize, Sort.by("publishedAt").descending())
        );
    }

    public VacancyEntity getVacancy(String id) {
        return repository.findById(id).orElse(null);
    }

    public boolean deleteVacancy(String id) {
        Optional<VacancyEntity> vacancyOpt = repository.findById(id);
        if (vacancyOpt.isPresent()) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }
}