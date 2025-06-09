package com.fedordemin.vacancyparser;

import com.fedordemin.vacancyparser.config.SortConfig;
import com.fedordemin.vacancyparser.entities.VacancyEntity;
import com.fedordemin.vacancyparser.utils.format.SortStrategy.DefaultSortStrategy;
import com.fedordemin.vacancyparser.utils.format.SortStrategy.TitleSortStrategy;
import com.fedordemin.vacancyparser.utils.format.SortStrategy.DateSortStrategy;
import com.fedordemin.vacancyparser.utils.format.SortStrategy.SalarySortStrategy;
import com.fedordemin.vacancyparser.utils.format.SortStrategy.VacancySortStrategy;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

@SpringJUnitConfig(classes = {SortConfig.class, SortConfigTests.DummySortStrategiesConfig.class})
public class SortConfigTests {

    @TestConfiguration
    static class DummySortStrategiesConfig {

        @Bean
        public DefaultSortStrategy defaultSortStrategy() {
            return new DefaultSortStrategy() {
                @Override
                public List<VacancyEntity> sort(Iterable<VacancyEntity> vacancies) {
                    return Collections.emptyList();
                }
                @Override
                public String getSortDescription() {
                    return "Default strategy";
                }
            };
        }

        @Bean
        public TitleSortStrategy titleSortStrategy() {
            return new TitleSortStrategy() {
                @Override
                public List<VacancyEntity> sort(Iterable<VacancyEntity> vacancies) {
                    return Collections.emptyList();
                }
                @Override
                public String getSortDescription() {
                    return "Title strategy";
                }
            };
        }

        @Bean
        public DateSortStrategy dateSortStrategy() {
            return new DateSortStrategy() {
                @Override
                public List<VacancyEntity> sort(Iterable<VacancyEntity> vacancies) {
                    return Collections.emptyList();
                }
                @Override
                public String getSortDescription() {
                    return "Date strategy";
                }
            };
        }

        @Bean
        public SalarySortStrategy salarySortStrategy() {
            return new SalarySortStrategy() {
                @Override
                public List<VacancyEntity> sort(Iterable<VacancyEntity> vacancies) {
                    return Collections.emptyList();
                }
                @Override
                public String getSortDescription() {
                    return "Salary strategy";
                }
            };
        }
    }

    @Autowired
    private Map<String, VacancySortStrategy> sortStrategies;

    @Test
    public void testSortStrategiesBeanCreation() {
        assertNotNull(sortStrategies, "Sort strategies map не должна быть null");
        assertEquals(4, sortStrategies.size(), "Map должна содержать 4 стратегии");
    }

    @Test
    public void testDefaultSortStrategy() {
        System.out.println(sortStrategies.keySet());
        assertTrue(sortStrategies.containsKey("defaultSortStrategy"), "Map должна содержать ключ 'default'");
        assertEquals("Default strategy", sortStrategies.get("defaultSortStrategy").getSortDescription());
    }

    @Test
    public void testTitleSortStrategy() {
        assertTrue(sortStrategies.containsKey("titleSortStrategy"), "Map должна содержать ключ 'title'");
        assertEquals("Title strategy", sortStrategies.get("titleSortStrategy").getSortDescription());
    }

    @Test
    public void testDateSortStrategy() {
        assertTrue(sortStrategies.containsKey("dateSortStrategy"), "Map должна содержать ключ 'date'");
        assertEquals("Date strategy", sortStrategies.get("dateSortStrategy").getSortDescription());
    }

    @Test
    public void testSalarySortStrategy() {
        assertTrue(sortStrategies.containsKey("salarySortStrategy"), "Map должна содержать ключ 'salary'");
        assertEquals("Salary strategy", sortStrategies.get("salarySortStrategy").getSortDescription());
    }
}