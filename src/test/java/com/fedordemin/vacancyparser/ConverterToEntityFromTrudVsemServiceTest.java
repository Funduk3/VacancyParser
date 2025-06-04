package com.fedordemin.vacancyparser;

import com.fedordemin.vacancyparser.entities.VacancyEntity;
import com.fedordemin.vacancyparser.models.TrudVsem.VacancyTrudVsem;
import com.fedordemin.vacancyparser.services.converters.ConverterToEntityFromTrudVsemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class ConverterToEntityFromTrudVsemServiceTest {

    private ConverterToEntityFromTrudVsemService converter;

    @BeforeEach
    void setUp() {
        converter = new ConverterToEntityFromTrudVsemService();
    }

    @Test
    void testConvertEntityFromTrudVsem_AllFields() {
        VacancyTrudVsem vacancy = Mockito.mock(VacancyTrudVsem.class);

        when(vacancy.getId()).thenReturn("1");
        when(vacancy.getName()).thenReturn("Developer");
        LocalDate creationDate = LocalDate.now();
        when(vacancy.getCreationDate()).thenReturn(creationDate);
        when(vacancy.getSalary_min()).thenReturn(1000);
        when(vacancy.getSalary_max()).thenReturn(2000);
        when(vacancy.getDuty()).thenReturn("Job duty");

        VacancyTrudVsem.Requirement requirement = Mockito.mock(VacancyTrudVsem.Requirement.class);
        when(requirement.getExperience()).thenReturn(Integer.valueOf("3"));
        when(vacancy.getRequirement()).thenReturn(requirement);

        when(vacancy.getEmployment()).thenReturn("Full time");
        when(vacancy.getUrl()).thenReturn("http://example.com");

        VacancyTrudVsem.Company company = Mockito.mock(VacancyTrudVsem.Company.class);
        when(company.getId()).thenReturn("comp1");
        when(company.getName()).thenReturn("CompanyName");
        when(vacancy.getCompany()).thenReturn(company);

        VacancyTrudVsem.Addresses.Address address = Mockito.mock(VacancyTrudVsem.Addresses.Address.class);
        when(address.getLocation()).thenReturn("CityName");
        when(vacancy.getAddress()).thenReturn(address);

        VacancyEntity entity = converter.convertEntityFromTrudVsem(vacancy);

        assertNotNull(entity);
        assertEquals("1", entity.getId());
        assertEquals("Developer", entity.getName());
        assertEquals(creationDate.atStartOfDay(), entity.getPublished_at());
        assertEquals(1000, entity.getSalaryFrom());
        assertEquals(2000, entity.getSalaryTo());
        assertEquals("Job duty", entity.getRequirements());
        assertEquals("3", entity.getExperienceName());
        assertEquals("Full time", entity.getScheduleName());
        assertEquals("http://example.com", entity.getAlternate_url());
        assertEquals("comp1", entity.getEmployerId());
        assertEquals("CompanyName", entity.getEmployerName());
        assertEquals("CityName", entity.getCity());
    }

    @Test
    void testConvertEntityFromTrudVsem_ExceptionHandling() {
        VacancyTrudVsem vacancy = Mockito.mock(VacancyTrudVsem.class);
        when(vacancy.getId()).thenReturn("3");
        when(vacancy.getName()).thenThrow(new RuntimeException("Test exception"));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                converter.convertEntityFromTrudVsem(vacancy)
        );
        assertEquals("Error converting vacancy 3: Test exception", exception.getMessage());
    }
}