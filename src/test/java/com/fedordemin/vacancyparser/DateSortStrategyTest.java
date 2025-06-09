package com.fedordemin.vacancyparser;

import com.fedordemin.vacancyparser.entities.VacancyEntity;
import com.fedordemin.vacancyparser.utils.format.SortStrategy.DateSortStrategy;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DateSortStrategyTest {

    private final DateSortStrategy strategy = new DateSortStrategy();

    @Test
    void testSortByDate() {
        VacancyEntity v1 = VacancyEntity.builder().id("1").published_at(LocalDate.of(2021, 1, 10).atStartOfDay()).build();
        VacancyEntity v2 = VacancyEntity.builder().id("2").published_at(LocalDate.of(2020, 5, 20).atStartOfDay()).build();
        VacancyEntity v3 = VacancyEntity.builder().id("3").published_at(LocalDate.of(2019, 3, 15).atStartOfDay()).build();
        List<VacancyEntity> vacancies = Arrays.asList(v1, v2, v3);
        List<VacancyEntity> result = strategy.sort(vacancies);
        assertEquals(LocalDate.of(2019, 3, 15).atStartOfDay(), result.get(0).getPublished_at());
        assertEquals(LocalDate.of(2020, 5, 20).atStartOfDay(), result.get(1).getPublished_at());
        assertEquals(LocalDate.of(2021, 1, 10).atStartOfDay(), result.get(2).getPublished_at());
    }

    @Test
    void testGetSortDescription() {
        assertEquals("Sorted by: Published Date", strategy.getSortDescription());
    }
}