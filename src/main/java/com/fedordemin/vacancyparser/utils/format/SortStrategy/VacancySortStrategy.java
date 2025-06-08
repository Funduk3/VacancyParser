package com.fedordemin.vacancyparser.utils.format.SortStrategy;

import com.fedordemin.vacancyparser.entities.VacancyEntity;
import java.util.List;

public interface VacancySortStrategy {
    List<VacancyEntity> sort(Iterable<VacancyEntity> vacancies);
    String getSortDescription();
}