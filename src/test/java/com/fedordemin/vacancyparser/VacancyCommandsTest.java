package com.fedordemin.vacancyparser;

import com.fedordemin.vacancyparser.components.VacancyCommands;
import com.fedordemin.vacancyparser.entities.VacancyEntity;
import com.fedordemin.vacancyparser.services.VacancyService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class VacancyCommandsTest {

    private VacancyService vacancyService;
    private VacancyCommands vacancyCommands;
    private PrintStream originalOut;
    private ByteArrayOutputStream baos;

    @BeforeEach
    void setUp() {
        vacancyService = mock(VacancyService.class);
        vacancyCommands = new VacancyCommands(vacancyService);
        baos = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(baos));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    void testFindByIdFound() {
        VacancyEntity vacancy = new VacancyEntity();
        when(vacancyService.getVacancy("1")).thenReturn(vacancy);
        when(vacancyService.formatVacancy(vacancy)).thenReturn("Formatted Vacancy");

        String result = vacancyCommands.findById("1");
        assertEquals("Formatted Vacancy", result);
        verify(vacancyService).getVacancy("1");
        verify(vacancyService).formatVacancy(vacancy);
    }

    @Test
    void testFindByIdNotFound() {
        when(vacancyService.getVacancy("1")).thenReturn(null);

        String result = vacancyCommands.findById("1");
        assertEquals("Vacancy not found with ID: 1", result);
        verify(vacancyService).getVacancy("1");
        verify(vacancyService, never()).formatVacancy(any());
    }

    @Test
    void testShowVacancies() {
        List<VacancyEntity> list = Collections.singletonList(new VacancyEntity());
        Page<VacancyEntity> page = new PageImpl<>(list);
        when(vacancyService.getVacancies("Java", "Company", 1000, 2000, "Area", 0, 10)).thenReturn(page);
        when(vacancyService.formatResult(page)).thenReturn("Formatted Page");

        String result = vacancyCommands.showVacancies("Java", "Company", 1000, 2000, "Area", 0, 10);
        assertEquals("Formatted Page", result);
        verify(vacancyService).getVacancies("Java", "Company", 1000, 2000, "Area", 0, 10);
        verify(vacancyService).formatResult(page);
    }

    @Test
    void testFetchVacancies() throws Exception {
        doNothing().when(vacancyService).fetchVacancies("Java", "Company", "Area", "hh.ru");

        String result = vacancyCommands.fetchVacancies("Java", "Company", "Area", "hh.ru");
        assertEquals("Successfully fetched vacancies", result);
        verify(vacancyService).fetchVacancies("Java", "Company", "Area", "hh.ru");
    }

    @Test
    void testDeleteVacancySuccess() {
        when(vacancyService.deleteVacancy("1")).thenReturn(true);

        String result = vacancyCommands.deleteVacancy("1");
        assertEquals("Vacancy with ID 1 was successfully deleted", result);
        verify(vacancyService).deleteVacancy("1");
    }

    @Test
    void testDeleteVacancyNotFound() {
        when(vacancyService.deleteVacancy("1")).thenReturn(false);

        String result = vacancyCommands.deleteVacancy("1");
        assertEquals("Vacancy not found with ID: 1", result);
        verify(vacancyService).deleteVacancy("1");
    }

    @Test
    void testExportEmployees() {
        when(vacancyService.export("csv", "vacancies")).thenReturn("Export done");

        String result = vacancyCommands.exportEmployees("csv", "vacancies");
        assertEquals("Export done", result);
        verify(vacancyService).export("csv", "vacancies");
    }

    @Test
    void testShowHistory() {
        when(vacancyService.formatHistory("all")).thenReturn("History info");

        String result = vacancyCommands.showHistory("all");
        assertEquals("History info", result);
        verify(vacancyService).formatHistory("all");
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void testSendNotificationNoNewVacancy() throws InterruptedException {
        List<VacancyEntity> list = Collections.singletonList(new VacancyEntity());
        Page<VacancyEntity> initialPage = new PageImpl<>(list);
        Page<VacancyEntity> nextPage = new PageImpl<>(list);

        when(vacancyService.getVacancies("Java", "Company", null, null, "Area", 0, 5))
                .thenReturn(initialPage);
        when(vacancyService.getVacancies("Java", "Company", null, null, "Area", 0, 6))
                .thenReturn(nextPage);
        when(vacancyService.formatResult(initialPage)).thenReturn("Initial formatted result");

        String result = vacancyCommands.sendNotification("Java", "Company", null, null, "Area");
        assertEquals("Фоновый поиск вакансий запущен", result);
        
        TimeUnit.MILLISECONDS.sleep(500);
        
        verify(vacancyService).getVacancies("Java", "Company", null, null, "Area", 0, 5);
        verify(vacancyService, times(1)).getVacancies("Java", "Company", null, null, "Area", 0, 6);
        verify(vacancyService, never()).formatVacancy(any());
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void testSendNotificationWithNewVacancy() throws InterruptedException {
        VacancyEntity vacancy1 = new VacancyEntity();
        VacancyEntity vacancy2 = new VacancyEntity();
        List<VacancyEntity> initialList = Collections.singletonList(vacancy1);
        Page<VacancyEntity> initialPage = new PageImpl<>(initialList);
        List<VacancyEntity> newList = Arrays.asList(vacancy1, vacancy2);
        Page<VacancyEntity> nextPage = new PageImpl<>(newList);

        when(vacancyService.getVacancies("Java", "Company", null, null, "Area", 0, 5))
                .thenReturn(initialPage);
        when(vacancyService.getVacancies("Java", "Company", null, null, "Area", 0, 6))
                .thenReturn(nextPage);
        when(vacancyService.formatResult(initialPage)).thenReturn("Initial formatted result");
        when(vacancyService.formatVacancy(vacancy2)).thenReturn("Formatted new vacancy");

        String result = vacancyCommands.sendNotification("Java", "Company", null, null, "Area");
        assertEquals("Фоновый поиск вакансий запущен", result);
        
        TimeUnit.MILLISECONDS.sleep(500);
        
        verify(vacancyService).getVacancies("Java", "Company", null, null, "Area", 0, 5);
        verify(vacancyService, times(1)).getVacancies("Java", "Company", null, null, "Area", 0, 6);
        verify(vacancyService).formatVacancy(vacancy2);

        String output = baos.toString();
        assertTrue(output.contains("Новая вакансия найдена!"));
    }
}