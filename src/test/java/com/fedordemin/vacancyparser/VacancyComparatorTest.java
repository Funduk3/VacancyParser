package com.fedordemin.vacancyparser;

import com.fedordemin.vacancyparser.entities.VacancyEntity;
import com.fedordemin.vacancyparser.services.VacancyComparator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class VacancyComparatorTest {

    private VacancyComparator comparator;

    @BeforeEach
    void setup() {
        comparator = new VacancyComparator();
    }

    @Test
    void testIsDuplicate_AllFieldsEqual_IgnoringCase() {
        VacancyEntity v1 = new VacancyEntity();
        v1.setName("Developer");
        v1.setEmployerName("Company");
        v1.setDescription("Job Description");

        VacancyEntity v2 = new VacancyEntity();
        v2.setName("developer");
        v2.setEmployerName("company");
        v2.setDescription("job description");

        assertTrue(comparator.isDuplicate(v1, v2));
    }

    @Test
    void testIsDuplicate_DifferentNames() {
        VacancyEntity v1 = new VacancyEntity();
        v1.setName("Developer");
        v1.setEmployerName("Company");
        v1.setDescription("Job Description");

        VacancyEntity v2 = new VacancyEntity();
        v2.setName("Tester");
        v2.setEmployerName("Company");
        v2.setDescription("Job Description");

        assertFalse(comparator.isDuplicate(v1, v2));
    }

    @Test
    void testIsDuplicate_DifferentEmployerNames() {
        VacancyEntity v1 = new VacancyEntity();
        v1.setName("Developer");
        v1.setEmployerName("CompanyA");
        v1.setDescription("Job Description");

        VacancyEntity v2 = new VacancyEntity();
        v2.setName("Developer");
        v2.setEmployerName("CompanyB");
        v2.setDescription("Job Description");

        assertFalse(comparator.isDuplicate(v1, v2));
    }

    @Test
    void testIsDuplicate_DifferentDescriptions() {
        VacancyEntity v1 = new VacancyEntity();
        v1.setName("Developer");
        v1.setEmployerName("Company");
        v1.setDescription("Job Description");

        VacancyEntity v2 = new VacancyEntity();
        v2.setName("Developer");
        v2.setEmployerName("Company");
        v2.setDescription("Different Description");

        assertFalse(comparator.isDuplicate(v1, v2));
    }

    @Test
    void testIsDuplicate_NullVacancies() {
        VacancyEntity v1 = null;
        VacancyEntity v2 = new VacancyEntity();
        v2.setName("Developer");

        assertFalse(comparator.isDuplicate(v1, v2));
        assertFalse(comparator.isDuplicate(v2, null));
        assertFalse(comparator.isDuplicate(null, null));
    }

    @Test
    void testIsDuplicate_NullDescriptionsBoth() {
        VacancyEntity v1 = new VacancyEntity();
        v1.setName("Developer");
        v1.setEmployerName("Company");
        v1.setDescription(null);

        VacancyEntity v2 = new VacancyEntity();
        v2.setName("Developer");
        v2.setEmployerName("Company");
        v2.setDescription(null);

        assertTrue(comparator.isDuplicate(v1, v2));
    }
}
