package com.fedordemin.vacancyparser;

import com.fedordemin.vacancyparser.entities.VacancyEntity;
import com.fedordemin.vacancyparser.utils.format.SortStrategy.TitleSortStrategy;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class TitleSortStrategyTest {

    private final TitleSortStrategy strategy = new TitleSortStrategy();

    @Test
    void testSortByTitle() {
        VacancyEntity v1 = VacancyEntity.builder().id("1").name("Charlie").build();
        VacancyEntity v2 = VacancyEntity.builder().id("2").name("alpha").build();
        VacancyEntity v3 = VacancyEntity.builder().id("3").name("Beta").build();
        List<VacancyEntity> vacancies = Arrays.asList(v1, v2, v3);
        List<VacancyEntity> result = strategy.sort(vacancies);
        assertEquals("alpha", result.get(0).getName());
        assertEquals("Beta", result.get(1).getName());
        assertEquals("Charlie", result.get(2).getName());
    }

    @Test
    void testGetSortDescription() {
        assertTrue(strategy.getSortDescription().contains("Sorted by: Alphabetic title of vacancies"));
    }
}