package com.fedordemin.vacancyparser.services.converters;

import com.fedordemin.vacancyparser.models.VacancyTrudVsem;
import com.fedordemin.vacancyparser.entities.VacancyEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ConverterToEntityFromTrudVsemService {
    private static final Logger log = LoggerFactory.getLogger(ConverterToEntityFromTrudVsemService.class);

    public VacancyEntity convertEntityFromTrudVsem(VacancyTrudVsem vacancyTrudVsem) {
        try {
            log.error(vacancyTrudVsem.toString());
            VacancyEntity entity = VacancyEntity.builder()
                    .id(vacancyTrudVsem.getId())
                    .name(vacancyTrudVsem.getName())
                    .published_at(vacancyTrudVsem.getCreationDate().atStartOfDay())
                    .salaryFrom(vacancyTrudVsem.getSalary_min())
                    .salaryTo(vacancyTrudVsem.getSalary_max())
                    .requirements(vacancyTrudVsem.getDuty())
                    .experienceName(String.valueOf(vacancyTrudVsem.getRequirement().getExperience()))
                    .scheduleName(vacancyTrudVsem.getEmployment())
                    .alternate_url(vacancyTrudVsem.getUrl())
                    .employerId(vacancyTrudVsem.getCompany() != null ? vacancyTrudVsem.getCompany().getId() : null)
                    .employerName(vacancyTrudVsem.getCompany() != null ? vacancyTrudVsem.getCompany().getName() : null)
                    .city(vacancyTrudVsem.getAddress() != null ? vacancyTrudVsem.getAddress().getLocation() : null)
                    .build();
            return entity;
        } catch (Exception e) {
            log.warn("Error converting vacancy {}: {}", vacancyTrudVsem.getId(), e.getMessage());
            return null;
        }
    }
}
