package com.fedordemin.vacancyparser.config;

import com.fedordemin.vacancyparser.utils.format.SortStrategy.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class SortConfig {

    @Bean
    public Map<String, VacancySortStrategy> sortStrategies(
            DefaultSortStrategy defaultSortStrategy,
            TitleSortStrategy titleSortStrategy,
            DateSortStrategy dateSortStrategy,
            SalarySortStrategy salarySortStrategy)
    {
        Map<String, VacancySortStrategy> strategies = new HashMap<>();
        strategies.put("default", defaultSortStrategy);
        strategies.put("title", titleSortStrategy);
        strategies.put("date", dateSortStrategy);
        strategies.put("salary", salarySortStrategy);
        return strategies;
    }
}
