package com.fedordemin.vacancyparser.utils.format.SortStrategy;

import com.fedordemin.vacancyparser.entities.VacancyEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
public class DefaultSortStrategy implements VacancySortStrategy {
    @Override
    public List<VacancyEntity> sort(Iterable<VacancyEntity> vacancies) {
        return StreamSupport.stream(vacancies.spliterator(), false)
                .collect(Collectors.toList());
    }

    @Override
    public String getSortDescription() {
        return "Sorted by: Default (ID)";
    }
}
