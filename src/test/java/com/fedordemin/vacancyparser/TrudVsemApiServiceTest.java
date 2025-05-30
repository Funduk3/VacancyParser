package com.fedordemin.vacancyparser;

import com.fedordemin.vacancyparser.entities.VacancyEntity;
import com.fedordemin.vacancyparser.models.TrudVsem.VacancyResponseTrudVsem;
import com.fedordemin.vacancyparser.services.HistoryWriterService;
import com.fedordemin.vacancyparser.services.api.TrudVsemApiService;
import com.fedordemin.vacancyparser.services.converters.ConverterToEntityFromTrudVsemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

public class TrudVsemApiServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ConverterToEntityFromTrudVsemService converterToEntityFromTrudVsemService;

    @Mock
    private HistoryWriterService historyWriterService;

    @InjectMocks
    private TrudVsemApiService trudVsemApiService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSearchVacancies_whenResponseSuccessful() {
        String text = "developer";
        String area = "1";

        VacancyResponseTrudVsem mockResponse = new VacancyResponseTrudVsem();
        ResponseEntity<VacancyResponseTrudVsem> responseEntity =
                new ResponseEntity<>(mockResponse, HttpStatus.OK);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(VacancyResponseTrudVsem.class)))
                .thenReturn(responseEntity);

        VacancyResponseTrudVsem result = trudVsemApiService.searchVacancies(text, area);
        assertNotNull(result);
    }

    @Test
    void testSearchVacancies_whenResponseError() {
        String text = "developer";
        String area = "1";

        ResponseEntity<VacancyResponseTrudVsem> responseEntity =
                new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(VacancyResponseTrudVsem.class)))
                .thenReturn(responseEntity);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            trudVsemApiService.searchVacancies(text, area);
        });
        assertTrue(exception.getMessage().contains("Ошибка при запросе TrudVsem"));
    }

    @Test
    void testFetchTrudVsemApi_whenResponseSuccessful() {
        String searchText = "developer";
        String area = "1";
        Boolean isByUser = true;

        VacancyResponseTrudVsem responseMock = mock(VacancyResponseTrudVsem.class);
        VacancyResponseTrudVsem.Results resultsMock = mock(VacancyResponseTrudVsem.Results.class);
        when(responseMock.getResults()).thenReturn(resultsMock);
        VacancyResponseTrudVsem.VacancyContainer containerMock = mock(VacancyResponseTrudVsem.VacancyContainer.class);
        when(resultsMock.getVacancies()).thenReturn(Collections.singletonList(containerMock));

        ResponseEntity<VacancyResponseTrudVsem> responseEntity =
                new ResponseEntity<>(responseMock, HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(VacancyResponseTrudVsem.class)))
                .thenReturn(responseEntity);

        VacancyEntity dummyEntity = new VacancyEntity();
        when(converterToEntityFromTrudVsemService.convertEntityFromTrudVsem(any()))
                .thenReturn(dummyEntity);

        List<VacancyEntity> result = trudVsemApiService.fetchTrudVsemApi(searchText, area, isByUser);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(dummyEntity, result.get(0));
        verify(historyWriterService, times(1)).saveToHistory(isByUser, dummyEntity);
    }
}