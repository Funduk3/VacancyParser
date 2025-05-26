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
    void testSendNotification() {
        when(vacancyService.sendNotification("Java", "Company", 1000,
                2000, "Area"))
                .thenReturn("Notification Started");

        String result = vacancyCommands.sendNotification("Java", "Company", 1000,
                2000, "Area");

        assertEquals("Фоновый поиск вакансий запущен", result);
        assertTrue(baos.toString().contains("Notification Started"));
        verify(vacancyService).sendNotification("Java", "Company", 1000,
                2000, "Area");
    }
}