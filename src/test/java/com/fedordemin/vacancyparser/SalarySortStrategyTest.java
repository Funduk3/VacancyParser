package com.fedordemin.vacancyparser;

import com.fedordemin.vacancyparser.entities.VacancyEntity;
import com.fedordemin.vacancyparser.utils.format.SortStrategy.SalarySortStrategy;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class SalarySortStrategyTest {

    private final SalarySortStrategy strategy = new SalarySortStrategy();

    @Test
    void testSortBySalary() {
        VacancyEntity v1 = VacancyEntity.builder().id("1").salaryFrom(1000).salaryTo(2000).build(); // сумма 3000
        VacancyEntity v2 = VacancyEntity.builder().id("2").salaryFrom(800).salaryTo(1200).build();  // сумма 2000
        VacancyEntity v3 = VacancyEntity.builder().id("3").salaryFrom(1500).salaryTo(1500).build(); // сумма 3000
        List<VacancyEntity> vacancies = Arrays.asList(v1, v2, v3);
        List<VacancyEntity> result = strategy.sort(vacancies);
        int sum0 = (result.get(0).getSalaryFrom() + result.get(0).getSalaryTo());
        int sum1 = (result.get(1).getSalaryFrom() + result.get(1).getSalaryTo());
        int sum2 = (result.get(2).getSalaryFrom() + result.get(2).getSalaryTo());
        assertEquals(2000, sum0);
        assertTrue((sum1 == 3000 && sum2 == 3000));
    }

    @Test
    void testGetSortDescription() {
        assertEquals("Sorted by: Salary", strategy.getSortDescription());
    }
}
