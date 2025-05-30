package com.fedordemin.vacancyparser;

import com.fedordemin.vacancyparser.entities.VacancyEntity;
import com.fedordemin.vacancyparser.services.secondLayer.VacancyExportService;
import com.fedordemin.vacancyparser.services.secondLayer.VacancyFetcherService;
import com.fedordemin.vacancyparser.services.secondLayer.VacancyManagementService;
import com.fedordemin.vacancyparser.services.secondLayer.VacancyNotificationService;
import com.fedordemin.vacancyparser.services.MainFacade.VacancyFacadeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VacancyFacadeServiceTest {

    @Mock
    private VacancyFetcherService vacancyFetcherService;

    @Mock
    private VacancyExportService vacancyExportService;

    @Mock
    private VacancyManagementService vacancyManagementService;

    @Mock
    private VacancyNotificationService vacancyNotificationService;

    @InjectMocks
    private VacancyFacadeService vacancyFacadeService;

    private VacancyEntity testVacancy;
    private Page<VacancyEntity> testPage;

    @BeforeEach
    void setUp() {
        testVacancy = VacancyEntity.builder().id("1").build();
        testPage = new PageImpl<>(Collections.singletonList(testVacancy));
    }

    @Test
    void testGetVacancies() {
        when(vacancyManagementService.getVacancies(
                anyString(), anyString(), anyInt(), anyInt(), anyString(), anyInt(), anyInt()
        )).thenReturn(testPage);

        Page<VacancyEntity> result = vacancyFacadeService.getVacancies(
                "title", "company", 1000, 2000, "area", 0, 5);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(vacancyManagementService).getVacancies(
                "title", "company", 1000, 2000, "area", 0, 5);
    }

    @Test
    void testGetVacancy() {
        when(vacancyManagementService.getVacancy("1")).thenReturn(testVacancy);

        VacancyEntity result = vacancyFacadeService.getVacancy("1");

        assertNotNull(result);
        assertEquals("1", result.getId());
    }

    @Test
    void testGetAllVacancies() {
        List<VacancyEntity> vacancies = Collections.singletonList(testVacancy);
        when(vacancyManagementService.getAllVacancies()).thenReturn(vacancies);

        List<VacancyEntity> result = vacancyFacadeService.getAllVacancies();

        assertEquals(1, result.size());
        verify(vacancyManagementService).getAllVacancies();
    }

    @Test
    void testDeleteVacancy() {
        when(vacancyManagementService.deleteVacancy("1")).thenReturn(true);

        boolean result = vacancyFacadeService.deleteVacancy("1");

        assertTrue(result);
        verify(vacancyManagementService).deleteVacancy("1");
    }

    @Test
    void testFetchVacancies() {
        vacancyFacadeService.fetchVacancies("search", "company", "area", "hh.ru");

        verify(vacancyFetcherService).fetchVacancies(
                "search", "company", "area", "hh.ru", true);
    }

    @Test
    void testExport() {
        when(vacancyExportService.export("csv", "filename"))
                .thenReturn("Export complete");

        String result = vacancyFacadeService.export("csv", "filename");

        assertEquals("Export complete", result);
        verify(vacancyExportService).export("csv", "filename");
    }

    @Test
    void testSendNotification() {
        when(vacancyNotificationService.sendNotification(
                "Java", "Company", 1000, 2000, "Area"))
                .thenReturn("Notification sent");

        String result = vacancyFacadeService.sendNotification(
                "Java", "Company", 1000, 2000, "Area");

        assertEquals("Notification sent", result);
        verify(vacancyNotificationService).sendNotification(
                "Java", "Company", 1000, 2000, "Area");
    }
}