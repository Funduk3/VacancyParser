package com.fedordemin.vacancyparser;

import com.fedordemin.vacancyparser.entities.LogEntity;
import com.fedordemin.vacancyparser.entities.VacancyEntity;
import com.fedordemin.vacancyparser.services.FormatterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class FormatterServiceTest {

    private FormatterService formatterService;

    @BeforeEach
    void setUp() {
        formatterService = new FormatterService();
    }

    @Test
    void testFormatVacancy_WithAllFields() {
        VacancyEntity vacancy = new VacancyEntity();
        vacancy.setId("1");
        vacancy.setName("Developer");
        vacancy.setEmployerName("Company");
        vacancy.setSalaryFrom(1000);
        vacancy.setSalaryTo(2000);
        vacancy.setSalaryCurrency("USD");
        vacancy.setRequirements("Java, Spring Boot");
        vacancy.setPublished_at(LocalDateTime.now());
        vacancy.setAlternate_url("http://example.com");

        String result = formatterService.formatVacancy(vacancy);
        assertTrue(result.contains("ID: 1"));
        assertTrue(result.contains("Title: Developer"));
        assertTrue(result.contains("Company: Company"));
        assertTrue(result.contains("Salary: 1000 - 2000 USD"));
        assertTrue(result.contains("Requirements: Java, Spring Boot"));
        assertTrue(result.contains("Published: " + LocalDate.now().toString()));
        assertTrue(result.contains("Url: http://example.com"));
    }

    @Test
    void testFormatVacancy_WithNullFields() {
        VacancyEntity vacancy = new VacancyEntity();
        vacancy.setId("2");
        vacancy.setName("Tester");
        vacancy.setEmployerName("TestCompany");
        vacancy.setSalaryFrom(null);
        vacancy.setSalaryTo(null);
        vacancy.setSalaryCurrency(null);
        vacancy.setRequirements(null);
        vacancy.setPublished_at(null);
        vacancy.setAlternate_url(null);

        String result = formatterService.formatVacancy(vacancy);
        assertTrue(result.contains("ID: 2"));
        assertTrue(result.contains("Title: Tester"));
        assertTrue(result.contains("Company: TestCompany"));
        assertTrue(result.contains("Requirements: N/A"));
        assertTrue(result.contains("Published: N/A"));
        assertTrue(result.contains("Url: N/A"));
    }

    @Test
    void testFormatResult() {
        VacancyEntity vacancy1 = new VacancyEntity();
        vacancy1.setId("1");
        vacancy1.setName("Developer");
        vacancy1.setEmployerName("Company");
        vacancy1.setAlternate_url("http://example1.com");
        vacancy1.setPublished_at(LocalDateTime.now());

        VacancyEntity vacancy2 = new VacancyEntity();
        vacancy2.setId("2");
        vacancy2.setName("Tester");
        vacancy2.setEmployerName("TestCompany");
        vacancy2.setAlternate_url("http://example2.com");
        vacancy2.setPublished_at(LocalDateTime.now());

        List<VacancyEntity> list = Arrays.asList(vacancy1, vacancy2);
        Page<VacancyEntity> page = new PageImpl<>(list, PageRequest.of(0, 10), list.size());

        String result = formatterService.formatResult(page);
        assertTrue(result.contains("Page 1/1 (2 total)"));
        assertTrue(result.contains("ID: 1"));
        assertTrue(result.contains("ID: 2"));
    }

    @Test
    void testFormatLog_AllType() {
        LogEntity log = LogEntity.builder()
                .vacancyId("3")
                .type("added")
                .isByUser(true)
                .timestamp(LocalDateTime.now())
                .build();

        String result = formatterService.formatLog(log, "all");
        assertTrue(result.contains("Vacancy: 3"));
        assertTrue(result.contains("Action: added"));
        assertTrue(result.contains("Date of action:"));
    }

    @Test
    void testFormatLog_FilteredType() {
        LogEntity log = LogEntity.builder()
                .vacancyId("4")
                .type("deleted")
                .isByUser(true)
                .timestamp(LocalDateTime.now())
                .build();

        String result = formatterService.formatLog(log, "all");
        assertTrue(result.contains("Action: deleted"));

        result = formatterService.formatLog(log, "DELETED");
        assertTrue(result.contains("Action: deleted"));
    }

    @Test
    void testFormatLog_NotByUser() {
        LogEntity log = LogEntity.builder()
                .vacancyId("5")
                .type("added")
                .isByUser(false)
                .timestamp(LocalDateTime.now())
                .build();

        String result = formatterService.formatLog(log, "all");
        assertEquals("", result);
    }

    @Test
    void testFormatHistory() {
        LogEntity log1 = LogEntity.builder()
                .vacancyId("6")
                .type("added")
                .isByUser(true)
                .timestamp(LocalDateTime.now())
                .build();
        LogEntity log2 = LogEntity.builder()
                .vacancyId("7")
                .type("updated")
                .isByUser(true)
                .timestamp(LocalDateTime.now())
                .build();

        List<LogEntity> logs = Arrays.asList(log1, log2);
        String history = formatterService.formatHistory(logs, "all");

        assertTrue(history.contains("Vacancy: 6"));
        assertTrue(history.contains("Vacancy: 7"));
        assertTrue(history.contains("Action: added"));
        assertTrue(history.contains("Action: updated"));
    }
}