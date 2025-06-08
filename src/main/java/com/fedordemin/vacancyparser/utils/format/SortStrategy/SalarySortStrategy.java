package com.fedordemin.vacancyparser.utils.format.SortStrategy;

import com.fedordemin.vacancyparser.entities.VacancyEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
public class SalarySortStrategy implements VacancySortStrategy {
    @Override
    public List<VacancyEntity> sort(Iterable<VacancyEntity> vacancies) {
        return StreamSupport.stream(vacancies.spliterator(), false)
                .sorted((v1, v2) -> {
                    int salary1 = (v1.getSalaryFrom() != null ? v1.getSalaryFrom() : 0) +
                            (v1.getSalaryTo() != null ? v1.getSalaryTo() : 0);
                    int salary2 = (v2.getSalaryFrom() != null ? v2.getSalaryFrom() : 0) +
                            (v2.getSalaryTo() != null ? v2.getSalaryTo() : 0);
                    return Integer.compare(salary1, salary2);
                })
                .collect(Collectors.toList());
    }

    @Override
    public String getSortDescription() {
        return "Sorted by: Salary";
    }
}