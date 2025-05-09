package com.fedordemin.vacancyparser.services;

import com.fedordemin.vacancyparser.models.VacancyHhRu;
import com.fedordemin.vacancyparser.models.entities.VacancyEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ConverterToEntityService {
    private static final Logger log = LoggerFactory.getLogger(ConverterToEntityService.class);

    public VacancyEntity convertEntityFromHhRu(VacancyHhRu vacancyHhRu) {
        try {
            VacancyEntity entity = new VacancyEntity();
            entity.setId(vacancyHhRu.getId());
            entity.setName(vacancyHhRu.getName());
            entity.setAlternate_url(vacancyHhRu.getUrl());
            if (vacancyHhRu.getEmployer() != null) {
                entity.setEmployerId(vacancyHhRu.getEmployer().getId());
                entity.setEmployerName(vacancyHhRu.getEmployer().getName());
            }
            if (vacancyHhRu.getSalary() != null) {
                entity.setSalaryFrom(vacancyHhRu.getSalary().getFrom());
                entity.setSalaryTo(vacancyHhRu.getSalary().getTo());
                entity.setSalaryCurrency(vacancyHhRu.getSalary().getCurrency());
                entity.setSalaryGross(vacancyHhRu.getSalary().getGross());
            }
            if (vacancyHhRu.getAddress() != null) {
                entity.setCity(vacancyHhRu.getAddress().getCity());
                entity.setStreet(vacancyHhRu.getAddress().getStreet());
            }
            if (vacancyHhRu.getSchedule() != null) {
                entity.setScheduleName(vacancyHhRu.getSchedule().getName());
            }
            if (vacancyHhRu.getExperience() != null) {
                entity.setExperienceName(vacancyHhRu.getExperience().getName());
            }
            if (vacancyHhRu.getSnippet() != null) {
                entity.setRequirements(vacancyHhRu.getSnippet().getRequirement());
            }
            entity.setPublishedAt(vacancyHhRu.getPublishedAt());
            entity.setDescription(vacancyHhRu.getDescription());

            return entity;
        } catch (Exception e) {
            log.warn("Error converting vacancy {}: {}", vacancyHhRu.getId(), e.getMessage());
            return null;
        }
    }

    public List<VacancyEntity> convertEntitiesFromHhRu(List<VacancyHhRu> vacancies) {
        List<VacancyEntity> entities = new ArrayList<>();
        for (VacancyHhRu vacancyHhRu : vacancies) {
            VacancyEntity entity = convertEntityFromHhRu(vacancyHhRu);
            if (entity != null) {
                entities.add(entity);
            }
        }
        return entities;
    }
}
