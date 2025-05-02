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

@Service
public class VacancyService {
    private final VacancyRepo repository;

    @Autowired
    public VacancyService(VacancyRepo repository) {
        this.repository = repository;
    }

    public String printListVacancies() {
        StringBuilder sb = new StringBuilder();
        repository.findAll().forEach(vacancy ->
                sb.append(vacancy.getName()).append("\n")
        );
        return sb.toString();
    }

    public void saveVacancies(List<Vacancy> vacancies) {
        List<VacancyEntity> entities = new ArrayList<>();

        for (Vacancy vacancy : vacancies) {
            VacancyEntity entity = new VacancyEntity();
            entity.setId(vacancy.getId());
            entity.setName(vacancy.getName());

            if (vacancy.getSalary() != null) {
                entity.setSalaryFrom(vacancy.getSalary().getFrom());
                entity.setSalaryTo(vacancy.getSalary().getTo());
                entity.setSalaryCurrency(vacancy.getSalary().getCurrency());
                entity.setSalaryGross(vacancy.getSalary().getGross());
            }

            if (vacancy.getEmployer() != null) {
                entity.setEmployerId(vacancy.getEmployer().getId());
                entity.setEmployerName(vacancy.getEmployer().getName());
            }

            entity.setDescription(vacancy.getDescription());

            if (vacancy.getAddress() != null) {
                entity.setCity(vacancy.getAddress().getCity());
                entity.setStreet(vacancy.getAddress().getStreet());
            }

            entity.setPublishedAt(vacancy.getPublishedAt());

            entities.add(entity);
        }

        repository.saveAll(entities);
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
                PageRequest.of(page, size, Sort.by("publishedAt").descending())
        );
    }
}