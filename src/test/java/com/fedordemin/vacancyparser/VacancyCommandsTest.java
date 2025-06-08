package com.fedordemin.vacancyparser;

import com.fedordemin.vacancyparser.components.VacancyCommands;
import com.fedordemin.vacancyparser.entities.LogEntity;
import com.fedordemin.vacancyparser.entities.VacancyEntity;
import com.fedordemin.vacancyparser.services.HistoryWriterService;
import com.fedordemin.vacancyparser.services.MainFacade.VacancyFacadeService;
import com.fedordemin.vacancyparser.utils.format.VacancyFormatterUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class VacancyCommandsTest {

    private VacancyFacadeService vacancyFacadeService;
    private VacancyFormatterUtil vacancyFormatterUtil;
    private HistoryWriterService historyWriterService;
    private VacancyCommands vacancyCommands;
    private PrintStream originalOut;
    private ByteArrayOutputStream baos;

    @BeforeEach
    void setUp() {
        vacancyFacadeService = mock(VacancyFacadeService.class);
        vacancyFormatterUtil = mock(VacancyFormatterUtil.class);
        historyWriterService = mock(HistoryWriterService.class);
        vacancyCommands = new VacancyCommands(vacancyFacadeService, vacancyFormatterUtil, historyWriterService);
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
        when(vacancyFacadeService.getVacancy("1")).thenReturn(vacancy);
        when(vacancyFormatterUtil.formatVacancy(vacancy)).thenReturn("Formatted Vacancy");

        String result = vacancyCommands.findById("1");
        assertEquals("Formatted Vacancy", result);
        verify(vacancyFacadeService).getVacancy("1");
        verify(vacancyFormatterUtil).formatVacancy(vacancy);
    }

    @Test
    void testFindByIdNotFound() {
        when(vacancyFacadeService.getVacancy("1")).thenReturn(null);

        String result = vacancyCommands.findById("1");
        assertEquals("Vacancy not found with ID: 1", result);
        verify(vacancyFacadeService).getVacancy("1");
        verify(vacancyFormatterUtil, never()).formatVacancy(any());
    }

    @Test
    void testShowVacancies() {
        List<VacancyEntity> list = Collections.singletonList(new VacancyEntity());
        Page<VacancyEntity> page = new PageImpl<>(list);
        when(vacancyFacadeService.getVacancies("Java", "Company", 1000, 2000, "Area", 0, 10)).thenReturn(page);
        when(vacancyFormatterUtil.formatResult(page, "default")).thenReturn("Formatted Page");

        String result = vacancyCommands.showVacancies("Java", "Company", 1000, 2000, "Area", 0, 10, "default");
        assertEquals("Formatted Page", result);
        verify(vacancyFacadeService).getVacancies("Java", "Company", 1000, 2000, "Area", 0, 10);
        verify(vacancyFormatterUtil).formatResult(page, "default");
    }

    @Test
    void testFetchVacancies() throws Exception {
        doNothing().when(vacancyFacadeService).fetchVacancies("Java", "Company", "Area", "hh.ru");
        vacancyCommands.fetchVacancies("Java", "Company", "Area", "hh.ru");
        verify(vacancyFacadeService).fetchVacancies("Java", "Company", "Area", "hh.ru");
    }

    @Test
    void testDeleteVacancySuccess() {
        when(vacancyFacadeService.deleteVacancy("1")).thenReturn(true);

        String result = vacancyCommands.deleteVacancy("1");
        assertEquals("Vacancy with ID 1 was successfully deleted", result);
        verify(vacancyFacadeService).deleteVacancy("1");
    }

    @Test
    void testDeleteVacancyNotFound() {
        when(vacancyFacadeService.deleteVacancy("1")).thenReturn(false);

        String result = vacancyCommands.deleteVacancy("1");
        assertEquals("Vacancy not found with ID: 1", result);
        verify(vacancyFacadeService).deleteVacancy("1");
    }

    @Test
    void testExportEmployees() {
        when(vacancyFacadeService.export("csv", "vacancies")).thenReturn("Export done");

        String result = vacancyCommands.exportEmployees("csv", "vacancies");
        assertEquals("Export done", result);
        verify(vacancyFacadeService).export("csv", "vacancies");
    }

    @Test
    void testShowHistory() {
        List<LogEntity> logs = Collections.emptyList();
        when(historyWriterService.getAllLogs()).thenReturn(logs);
        when(vacancyFormatterUtil.formatHistory(logs, "all")).thenReturn("History info");

        String result = vacancyCommands.showHistory("all");

        assertEquals("History info", result);
        verify(historyWriterService).getAllLogs();
        verify(vacancyFormatterUtil).formatHistory(logs, "all");
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void testSendNotification() {
        when(vacancyFacadeService.sendNotification("Java", "Company", 1000,
                2000, "Area"))
                .thenReturn("Notification Started");

        String result = vacancyCommands.sendNotification("Java", "Company", 1000,
                2000, "Area");

        assertEquals("Фоновый поиск вакансий запущен", result);
        assertTrue(baos.toString().contains("Notification Started"));
        verify(vacancyFacadeService).sendNotification("Java", "Company", 1000,
                2000, "Area");
    }
}