package com.fedordemin.vacancyparser;

import com.fedordemin.vacancyparser.entities.VacancyEntity;
import com.fedordemin.vacancyparser.utils.format.SortStrategy.DefaultSortStrategy;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class DefaultSortStrategyTest {

    private final DefaultSortStrategy strategy = new DefaultSortStrategy();

    @Test
    void testSortReturnsSameOrder() {
        VacancyEntity v1 = VacancyEntity.builder().id("1").name("A").build();
        VacancyEntity v2 = VacancyEntity.builder().id("2").name("B").build();
        List<VacancyEntity> vacancies = Arrays.asList(v1, v2);
        List<VacancyEntity> result = strategy.sort(vacancies);
        assertEquals(vacancies, result);
    }

    @Test
    void testGetSortDescription() {
        assertEquals("Sorted by: Default (ID)", strategy.getSortDescription());
    }
}