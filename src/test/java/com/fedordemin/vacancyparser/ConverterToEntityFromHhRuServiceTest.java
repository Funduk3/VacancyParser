package com.fedordemin.vacancyparser;

import com.fedordemin.vacancyparser.entities.VacancyEntity;
import com.fedordemin.vacancyparser.models.HhRu.VacancyHhRu;
import com.fedordemin.vacancyparser.services.converters.ConverterToEntityFromHhRuService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class ConverterToEntityFromHhRuServiceTest {

    private ConverterToEntityFromHhRuService converter;

    @BeforeEach
    void setUp() {
        converter = new ConverterToEntityFromHhRuService();
    }

    @Test
    void testConvertEntityFromHhRu_AllFields() {
        VacancyHhRu vacancy = Mockito.mock(VacancyHhRu.class);
        when(vacancy.getId()).thenReturn("1");
        when(vacancy.getName()).thenReturn("Developer");
        when(vacancy.getUrl()).thenReturn("http://example.com");

        VacancyHhRu.Employer employer = Mockito.mock(VacancyHhRu.Employer.class);
        when(employer.getId()).thenReturn("emp1");
        when(employer.getName()).thenReturn("Company");
        when(vacancy.getEmployer()).thenReturn(employer);

        VacancyHhRu.Salary salary = Mockito.mock(VacancyHhRu.Salary.class);
        when(salary.getFrom()).thenReturn(1000);
        when(salary.getTo()).thenReturn(2000);
        when(salary.getCurrency()).thenReturn("USD");
        when(salary.getGross()).thenReturn(true);
        when(vacancy.getSalary()).thenReturn(salary);

        VacancyHhRu.Address address = Mockito.mock(VacancyHhRu.Address.class);
        when(address.getCity()).thenReturn("New York");
        when(address.getStreet()).thenReturn("5th Avenue");
        when(vacancy.getAddress()).thenReturn(address);

        VacancyHhRu.Schedule schedule = Mockito.mock(VacancyHhRu.Schedule.class);
        when(schedule.getName()).thenReturn("Full time");
        when(vacancy.getSchedule()).thenReturn(schedule);

        VacancyHhRu.Experience experience = Mockito.mock(VacancyHhRu.Experience.class);
        when(experience.getName()).thenReturn("3 years");
        when(vacancy.getExperience()).thenReturn(experience);

        VacancyHhRu.Snippet snippet = Mockito.mock(VacancyHhRu.Snippet.class);
        when(snippet.getRequirement()).thenReturn("<p>Java experience</p>");
        when(vacancy.getSnippet()).thenReturn(snippet);

        LocalDateTime published = LocalDateTime.now();
        when(vacancy.getPublished_at()).thenReturn(published);
        when(vacancy.getDescription()).thenReturn("Job description");

        VacancyEntity entity = converter.convertEntityFromHhRu(vacancy);

        assertNotNull(entity);
        assertEquals("1", entity.getId());
        assertEquals("Developer", entity.getName());
        assertEquals("http://example.com", entity.getAlternate_url());
        assertEquals("emp1", entity.getEmployerId());
        assertEquals("Company", entity.getEmployerName());
        assertEquals(1000, entity.getSalaryFrom());
        assertEquals(2000, entity.getSalaryTo());
        assertEquals("USD", entity.getSalaryCurrency());
        assertTrue(entity.getSalaryGross());
        assertEquals("New York", entity.getCity());
        assertEquals("5th Avenue", entity.getStreet());
        assertEquals("Full time", entity.getScheduleName());
        assertEquals("3 years", entity.getExperienceName());
        assertEquals("Java experience", entity.getRequirements());
        assertEquals(published, entity.getPublished_at());
        assertEquals("Job description", entity.getDescription());
    }

    @Test
    void testConvertEntityFromHhRu_NullNestedObjects() {
        VacancyHhRu vacancy = Mockito.mock(VacancyHhRu.class);
        when(vacancy.getId()).thenReturn("2");
        when(vacancy.getName()).thenReturn("Tester");
        when(vacancy.getUrl()).thenReturn(null);
        when(vacancy.getEmployer()).thenReturn(null);
        when(vacancy.getSalary()).thenReturn(null);
        when(vacancy.getAddress()).thenReturn(null);
        when(vacancy.getSchedule()).thenReturn(null);
        when(vacancy.getExperience()).thenReturn(null);
        when(vacancy.getSnippet()).thenReturn(null);
        when(vacancy.getPublished_at()).thenReturn(null);
        when(vacancy.getDescription()).thenReturn(null);

        VacancyEntity entity = converter.convertEntityFromHhRu(vacancy);

        assertNotNull(entity);
        assertEquals("2", entity.getId());
        assertEquals("Tester", entity.getName());
        assertNull(entity.getAlternate_url());
        assertNull(entity.getEmployerId());
        assertNull(entity.getEmployerName());
        assertNull(entity.getSalaryFrom());
        assertNull(entity.getSalaryTo());
        assertNull(entity.getSalaryCurrency());
        assertNull(entity.getSalaryGross());
        assertNull(entity.getCity());
        assertNull(entity.getStreet());
        assertNull(entity.getScheduleName());
        assertNull(entity.getExperienceName());
        assertNull(entity.getRequirements());
        assertNull(entity.getPublished_at());
        assertNull(entity.getDescription());
    }

    @Test
    void testConvertEntityFromHhRu_ExceptionHandling() {
        VacancyHhRu vacancy = Mockito.mock(VacancyHhRu.class);
        when(vacancy.getId()).thenReturn("3");
        when(vacancy.getName()).thenThrow(new RuntimeException("Test exception"));

        VacancyEntity entity = converter.convertEntityFromHhRu(vacancy);

        assertNull(entity);
    }
}