package com.fedordemin.vacancyparser.utils.format.SortStrategy;

import com.fedordemin.vacancyparser.entities.VacancyEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
public class DateSortStrategy implements VacancySortStrategy {
    @Override
    public List<VacancyEntity> sort(Iterable<VacancyEntity> vacancies) {
        return StreamSupport.stream(vacancies.spliterator(), false)
                .sorted((v1, v2) -> {
                    if (v1.getPublished_at() == null && v2.getPublished_at() == null) return 0;
                    if (v1.getPublished_at() == null) return 1;
                    if (v2.getPublished_at() == null) return -1;
                    return v1.getPublished_at().compareTo(v2.getPublished_at());
                })
                .collect(Collectors.toList());
    }

    @Override
    public String getSortDescription() {
        return "Sorted by: Published Date";
    }
}