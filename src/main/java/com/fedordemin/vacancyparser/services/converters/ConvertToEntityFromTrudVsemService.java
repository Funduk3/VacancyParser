package com.fedordemin.vacancyparser.services.converters;

import com.fedordemin.vacancyparser.models.VacancyTrudVsem;
import com.fedordemin.vacancyparser.models.entities.VacancyEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ConvertToEntityFromTrudVsemService {
    private static final Logger log = LoggerFactory.getLogger(ConvertToEntityFromTrudVsemService.class);

    public VacancyEntity convertEntityFromTrudVsem(VacancyTrudVsem vacancyTrudVsem) {
        try {
            VacancyEntity entity = new VacancyEntity();
            entity.setId(vacancyTrudVsem.getId());
            entity.setName(vacancyTrudVsem.getName());
            entity.setPublishedAt(vacancyTrudVsem.getCreationDate().atStartOfDay());
            entity.setSalaryFrom(vacancyTrudVsem.getSalary_min());
            entity.setSalaryTo(vacancyTrudVsem.getSalary_max());
            entity.setRequirements(vacancyTrudVsem.getDuty());
            entity.setScheduleName(vacancyTrudVsem.getEmployment());
            entity.setAlternate_url(vacancyTrudVsem.getUrl());

            if (vacancyTrudVsem.getCompany() != null) {
                entity.setEmployerId(vacancyTrudVsem.getCompany().getId());
                entity.setEmployerName(vacancyTrudVsem.getCompany().getName());
            }
            if (vacancyTrudVsem.getCategory() != null) {
                //
            }
            if (vacancyTrudVsem.getAddress() != null) {
                entity.setCity(vacancyTrudVsem.getAddress().getLocation());
            }
            if (vacancyTrudVsem.getRequirement() != null) {
                entity.setExperienceName(vacancyTrudVsem.getRequirement().getExperience().toString());
            }

            return entity;
        } catch (Exception e) {
            log.warn("Error converting vacancy {}: {}", vacancyTrudVsem.getId(), e.getMessage());
            return null;
        }
    }

    public List<VacancyEntity> convertEntitiesFromTrudVsem(List<VacancyTrudVsem> vacancies) {
        List<VacancyEntity> entities = new ArrayList<>();
        for (VacancyTrudVsem vacancyTrudVsem : vacancies) {
            VacancyEntity entity = convertEntityFromTrudVsem(vacancyTrudVsem);
            if (entity != null) {
                entities.add(entity);
            }
        }
        return entities;
    }

}
