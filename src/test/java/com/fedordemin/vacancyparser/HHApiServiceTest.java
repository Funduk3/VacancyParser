package com.fedordemin.vacancyparser;

import com.fedordemin.vacancyparser.entities.VacancyEntity;
import com.fedordemin.vacancyparser.models.HhRu.EmployerResponseHhRu;
import com.fedordemin.vacancyparser.models.HhRu.VacancyHhRu;
import com.fedordemin.vacancyparser.models.HhRu.VacancyResponseHhRu;
import com.fedordemin.vacancyparser.services.HistoryWriterService;
import com.fedordemin.vacancyparser.services.api.HHApiService;
import com.fedordemin.vacancyparser.services.converters.ConverterToEntityFromHhRuService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HHApiServiceTest {
    @Mock
    private ConverterToEntityFromHhRuService converterToEntityFromHhRuService;

    @Mock
    private HistoryWriterService historyWriterService;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private HHApiService hhApiService;

    @Test
    void testSearchVacanciesSuccess() {
        VacancyResponseHhRu vacancyResponse = new VacancyResponseHhRu();
        vacancyResponse.setItems(Collections.emptyList());
        ResponseEntity<VacancyResponseHhRu> responseEntity =
                new ResponseEntity<>(vacancyResponse, HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(VacancyResponseHhRu.class)))
                .thenReturn(responseEntity);

        VacancyResponseHhRu result = hhApiService.searchVacancies("Java", "1", "1", 0, 10);
        assertNotNull(result);
        verify(restTemplate).exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(VacancyResponseHhRu.class));
    }

    @Test
    void testSearchVacanciesFailure() {
        ResponseEntity<VacancyResponseHhRu> responseEntity =
                new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(VacancyResponseHhRu.class)))
                .thenReturn(responseEntity);

        VacancyResponseHhRu result = hhApiService.searchVacancies("Java", "1", "1", 0, 10);
        assertNull(result);
    }

    @Test
    void testFetchHhRuVacancies() {
        EmployerResponseHhRu employerResponse = new EmployerResponseHhRu();
        List<EmployerResponseHhRu.EmployerHhRu> employers = new ArrayList<>();
        EmployerResponseHhRu.EmployerHhRu employer = mock(EmployerResponseHhRu.EmployerHhRu.class);
        when(employer.getName()).thenReturn("TestCompany");
        when(employer.getId()).thenReturn("123");
        employers.add(employer);
        employerResponse.setItems(employers);
        ResponseEntity<EmployerResponseHhRu> employerResponseEntity =
                new ResponseEntity<>(employerResponse, HttpStatus.OK);
        lenient().when(restTemplate.exchange(argThat(url -> url.toString().contains("api.hh.ru/employers")),
                        eq(HttpMethod.GET), any(HttpEntity.class), eq(EmployerResponseHhRu.class)))
                .thenReturn(employerResponseEntity);

        VacancyResponseHhRu vacancyResponse = new VacancyResponseHhRu();
        vacancyResponse.setItems(Collections.emptyList());
        vacancyResponse.setPages(1);
        ResponseEntity<VacancyResponseHhRu> vacancyResponseEntity =
                new ResponseEntity<>(vacancyResponse, HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(VacancyResponseHhRu.class)))
                .thenReturn(vacancyResponseEntity);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                hhApiService.fetchHhRu("Java", "TestCompany", "1", true, 1, 10)
        );
        assertEquals("No more vacancies to fetch", exception.getMessage());
    }

    @Test
    void testGetCompanyIdByNameSuccess() {
        String companyName = "TestCompany";
        String expectedId = "123";

        EmployerResponseHhRu employerResponse = new EmployerResponseHhRu();
        List<EmployerResponseHhRu.EmployerHhRu> employers = new ArrayList<>();

        EmployerResponseHhRu.EmployerHhRu employer = mock(EmployerResponseHhRu.EmployerHhRu.class);
        when(employer.getName()).thenReturn(companyName);
        when(employer.getId()).thenReturn(expectedId);
        employers.add(employer);

        employerResponse.setItems(employers);

        ResponseEntity<EmployerResponseHhRu> responseEntity =
                new ResponseEntity<>(employerResponse, HttpStatus.OK);

        when(restTemplate.exchange(any(), eq(HttpMethod.GET), any(HttpEntity.class), eq(EmployerResponseHhRu.class)))
                .thenReturn(responseEntity);

        String result = hhApiService.getCompanyIdByName(companyName);
        assertEquals(expectedId, result);
    }

    @Test
    void testGetCompanyIdByNameNotFound() {
        String companyName = "UnknownCompany";

        EmployerResponseHhRu employerResponse = new EmployerResponseHhRu();
        employerResponse.setItems(Collections.emptyList());

        ResponseEntity<EmployerResponseHhRu> responseEntity =
                new ResponseEntity<>(employerResponse, HttpStatus.OK);
        when(restTemplate.exchange(any(), eq(HttpMethod.GET), any(HttpEntity.class), eq(EmployerResponseHhRu.class)))
                .thenReturn(responseEntity);

        String result = hhApiService.getCompanyIdByName(companyName);
        assertNull(result);
    }

    @Test
    void testFetchHhRu_processVacancies() {
        VacancyResponseHhRu vacancyResponse = new VacancyResponseHhRu();
        List<VacancyHhRu> vacancies = new ArrayList<>();
        VacancyHhRu vacancy1 = Mockito.mock(VacancyHhRu.class);
        VacancyHhRu vacancy2 = Mockito.mock(VacancyHhRu.class);
        vacancies.add(vacancy1);
        vacancies.add(vacancy2);
        vacancyResponse.setItems(vacancies);
        vacancyResponse.setPages(1);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class),
                eq(VacancyResponseHhRu.class)))
                .thenReturn(new ResponseEntity<>(vacancyResponse, HttpStatus.OK));

        VacancyEntity entity1 = new VacancyEntity();
        VacancyEntity entity2 = new VacancyEntity();
        entity1.setName("Vacancy 1");
        entity2.setName("Vacancy 2");
        when(converterToEntityFromHhRuService.convertEntityFromHhRu(vacancy1)).thenReturn(entity1);
        when(converterToEntityFromHhRuService.convertEntityFromHhRu(vacancy2)).thenReturn(entity2);

        List<VacancyEntity> result = hhApiService.fetchHhRu("Java", null, "1", true, 1, 10);

        assertEquals(2, result.size());
        verify(historyWriterService).saveToHistory(true, entity1);
        verify(historyWriterService).saveToHistory(true, entity2);
    }
}