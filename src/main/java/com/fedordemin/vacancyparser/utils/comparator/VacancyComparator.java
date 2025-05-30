package com.fedordemin.vacancyparser.utils.comparator;

import com.fedordemin.vacancyparser.entities.VacancyEntity;
import org.springframework.stereotype.Component;

@Component
public class VacancyComparator {
    public static boolean isDuplicate(VacancyEntity v1, VacancyEntity v2) {
        if (v1 == null || v2 == null) return false;

        boolean namesEqual = v1.getName() != null && v1.getName().equalsIgnoreCase(v2.getName());
        boolean employerEqual = v1.getEmployerName() != null && v1.getEmployerName().equalsIgnoreCase(v2.getEmployerName());
        boolean descriptionsEqual = (v1.getDescription() == null && v2.getDescription() == null)
                || (v1.getDescription() != null && v1.getDescription().equalsIgnoreCase(v2.getDescription()));

        return namesEqual && employerEqual && descriptionsEqual;
    }
}