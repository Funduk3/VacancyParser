package com.fedordemin.vacancyparser.services.converters;

import com.fedordemin.vacancyparser.models.HhRu.VacancyHhRu;
import com.fedordemin.vacancyparser.entities.VacancyEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ConverterToEntityFromHhRuService {
    private static final Logger log = LoggerFactory.getLogger(ConverterToEntityFromHhRuService.class);

    public VacancyEntity convertEntityFromHhRu(VacancyHhRu vacancyHhRu) {
        try {
            return VacancyEntity.builder()
                    .id(vacancyHhRu.getId())
                    .name(vacancyHhRu.getName())
                    .alternate_url(vacancyHhRu.getUrl())
                    .employerId(vacancyHhRu.getEmployer() != null ? vacancyHhRu.getEmployer().getId() : null)
                    .employerName(vacancyHhRu.getEmployer() != null ? vacancyHhRu.getEmployer().getName() : null)
                    .salaryFrom(vacancyHhRu.getSalary() != null ? vacancyHhRu.getSalary().getFrom() : null)
                    .salaryTo(vacancyHhRu.getSalary() != null ? vacancyHhRu.getSalary().getTo() : null)
                    .salaryCurrency(vacancyHhRu.getSalary() != null ? vacancyHhRu.getSalary().getCurrency() : null)
                    .salaryGross(vacancyHhRu.getSalary() != null ? vacancyHhRu.getSalary().getGross() : null)
                    .city(vacancyHhRu.getAddress() != null ? vacancyHhRu.getAddress().getCity() : null)
                    .street(vacancyHhRu.getAddress() != null ? vacancyHhRu.getAddress().getStreet() : null)
                    .scheduleName(vacancyHhRu.getSchedule() != null ? vacancyHhRu.getSchedule().getName() : null)
                    .experienceName(vacancyHhRu.getExperience() != null ? vacancyHhRu.getExperience().getName() : null)
                    .requirements((vacancyHhRu.getSnippet() != null
                            && vacancyHhRu.getSnippet().getRequirement() != null) ?
                            vacancyHhRu.getSnippet().getRequirement()
                                    .replaceAll("<[^>]*>", "") : null)
                    .published_at(vacancyHhRu.getPublished_at())
                    .description(vacancyHhRu.getDescription())
                    .build();
        } catch (Exception e) {
            log.warn("Error converting vacancy {}: {}", vacancyHhRu.getId(), e.getMessage());
            return null;
        }
    }
}