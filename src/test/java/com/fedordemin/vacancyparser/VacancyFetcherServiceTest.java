package com.fedordemin.vacancyparser;

import com.fedordemin.vacancyparser.entities.VacancyEntity;
import com.fedordemin.vacancyparser.repositories.VacancyRepo;
import com.fedordemin.vacancyparser.services.api.HHApiService;
import com.fedordemin.vacancyparser.services.api.TrudVsemApiService;
import com.fedordemin.vacancyparser.services.secondLayer.VacancyFetcherService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class VacancyFetcherServiceTest {

    @Mock
    private HHApiService hhApiService;

    @Mock
    private TrudVsemApiService trudVsemApiService;

    @Mock
    private VacancyRepo vacancyRepo;

    @InjectMocks
    private VacancyFetcherService vacancyFetcherService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(vacancyFetcherService, "pagesToFetch", 2);
        ReflectionTestUtils.setField(vacancyFetcherService, "perPage", 5);
    }

    @Test
    void testFetchVacancies_ForHHRu() {
        VacancyEntity vacancy = new VacancyEntity();
        vacancy.setId("1");
        List<VacancyEntity> vacancies = Arrays.asList(vacancy);
        when(hhApiService.fetchHhRu("search", "company", "area", true, 2, 5)).thenReturn(vacancies);

        vacancyFetcherService.fetchVacancies("search", "company", "area", "hh.ru", true);

        verify(hhApiService, times(1)).fetchHhRu("search", "company", "area", true, 2, 5);
        verify(vacancyRepo, times(1)).saveAll(vacancies);
    }

    @Test
    void testFetchVacancies_ForTrudvsemRu() {
        VacancyEntity vacancy = new VacancyEntity();
        vacancy.setId("2");
        List<VacancyEntity> vacancies = Arrays.asList(vacancy);
        when(trudVsemApiService.fetchTrudVsemApi("search", "area", true)).thenReturn(vacancies);

        vacancyFetcherService.fetchVacancies("search", null, "area", "trudvsem.ru", true);

        verify(trudVsemApiService, times(1)).fetchTrudVsemApi("search", "area", true);
        verify(vacancyRepo, times(1)).saveAll(vacancies);
    }

    @Test
    void testFetchVacancies_EmptyVacancyList() {
        when(hhApiService.fetchHhRu(null, null, null, false, 2, 5)).thenReturn(Collections.emptyList());

        vacancyFetcherService.fetchVacancies(null, null, null, "hh.ru", false);

        verify(hhApiService, times(1)).fetchHhRu(null, null, null, false, 2, 5);
        verify(vacancyRepo, never()).saveAll(any());
    }

    @Test
    void testScheduledFetchVacancies() throws Exception {
        VacancyEntity vacancy = new VacancyEntity();
        vacancy.setId("3");
        List<VacancyEntity> vacancies = new ArrayList<>();
        vacancies.add(vacancy);
        when(hhApiService.fetchHhRu(null, null, null, false, 2, 5)).thenReturn(vacancies);

        vacancyFetcherService.scheduledFetchVacancies();

        verify(hhApiService, times(1)).fetchHhRu(null, null, null, false, 2, 5);
        verify(vacancyRepo, times(1)).saveAll(vacancies);
    }
}