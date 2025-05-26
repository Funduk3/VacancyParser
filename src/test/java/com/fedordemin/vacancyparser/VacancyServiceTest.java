package com.fedordemin.vacancyparser;

import com.fedordemin.vacancyparser.entities.LogEntity;
import com.fedordemin.vacancyparser.entities.VacancyEntity;
import com.fedordemin.vacancyparser.repositories.VacancyRepo;
import com.fedordemin.vacancyparser.services.*;
import com.fedordemin.vacancyparser.utils.CsvUtil;
import com.fedordemin.vacancyparser.utils.JsonUtil;
import com.fedordemin.vacancyparser.utils.XlsxUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VacancyServiceTest {

    @Mock
    private VacancyRepo vacancyRepository;

    @Mock
    private VacancyFetcherService vacancyFetcherService;

    @Mock
    private FormatterService formatterService;

    @Mock
    private HistoryWriterService historyWriterService;

    @Mock
    private CsvUtil csvUtil;

    @Mock
    private XlsxUtil xlsxUtil;

    @Mock
    private JsonUtil jsonUtil;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private VacancyService vacancyService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(vacancyService, "defaultPageSize", 10);
    }

    @Test
    void testGetVacancies() {
        VacancyEntity vacancy = VacancyEntity.builder().id("1").build();
        List<VacancyEntity> list = Collections.singletonList(vacancy);
        Page<VacancyEntity> page = new PageImpl<>(list);
        when(vacancyRepository.search(
                any(), any(), any(), any(), any(), any(Pageable.class)
        )).thenReturn(page);

        Page<VacancyEntity> result = vacancyService.getVacancies("title", "company", 1000, 2000, "area", 0, 5);
        assertNotNull(result);
        verify(vacancyRepository, times(1)).search(
                eq("title"), eq("company"), eq(1000), eq(2000), eq("area"), any(Pageable.class)
        );
    }

    @Test
    void testGetVacancy_Found() {
        VacancyEntity vacancy = VacancyEntity.builder().id("1").build();
        when(vacancyRepository.findById("1")).thenReturn(Optional.of(vacancy));
        VacancyEntity result = vacancyService.getVacancy("1");
        assertNotNull(result);
        assertEquals("1", result.getId());
    }

    @Test
    void testGetVacancy_NotFound() {
        when(vacancyRepository.findById("1")).thenReturn(Optional.empty());
        VacancyEntity result = vacancyService.getVacancy("1");
        assertNull(result);
    }

    @Test
    void testGetAllVacancies() {
        List<VacancyEntity> vacancies = Collections.singletonList(VacancyEntity.builder().id("1").build());
        when(vacancyRepository.findAll()).thenReturn(vacancies);
        List<VacancyEntity> result = vacancyService.getAllVacancies();
        assertEquals(1, result.size());
        verify(vacancyRepository, times(1)).findAll();
    }

    @Test
    void testDeleteVacancy_Found() {
        VacancyEntity vacancy = VacancyEntity.builder().id("1").build();
        when(vacancyRepository.findById("1")).thenReturn(Optional.of(vacancy));
        boolean deleted = vacancyService.deleteVacancy("1");

        assertTrue(deleted);
        verify(historyWriterService, times(1)).write(any(LogEntity.class));
        verify(vacancyRepository, times(1)).deleteById("1");
    }

    @Test
    void testDeleteVacancy_NotFound() {
        when(vacancyRepository.findById("1")).thenReturn(Optional.empty());
        boolean deleted = vacancyService.deleteVacancy("1");

        assertFalse(deleted);
        verify(historyWriterService, never()).write(any());
        verify(vacancyRepository, never()).deleteById("1");
    }

    @Test
    void testFetchVacancies() {
        vacancyService.fetchVacancies("search", "company", "area", "hh.ru");
        verify(vacancyFetcherService, times(1))
                .fetchVacancies("search", "company", "area", "hh.ru", true);
    }

    @Test
    void testFormatResult() {
        Page<VacancyEntity> page = new PageImpl<>(Collections.singletonList(VacancyEntity.builder().id("1").build()));
        when(formatterService.formatResult(page)).thenReturn("formatted result");
        String result = vacancyService.formatResult(page);
        assertEquals("formatted result", result);
    }

    @Test
    void testFormatVacancy() {
        VacancyEntity vacancy = VacancyEntity.builder().id("1").build();
        when(formatterService.formatVacancy(vacancy)).thenReturn("formatted vacancy");
        String result = vacancyService.formatVacancy(vacancy);
        assertEquals("formatted vacancy", result);
    }

    @Test
    void testFormatHistory() {
        List<LogEntity> logs = Collections.singletonList(
                LogEntity.builder().vacancyId("1").type("deleted").timestamp(LocalDateTime.now()).build()
        );
        when(historyWriterService.getAllLogs()).thenReturn(logs);
        when(formatterService.formatHistory(logs, "all")).thenReturn("formatted history");
        String result = vacancyService.formatHistory("all");
        assertEquals("formatted history", result);
    }

    @Test
    void testExport_Csv() throws IOException {
        List<VacancyEntity> vacancies = Collections.singletonList(VacancyEntity.builder().id("1").build());
        when(vacancyRepository.findAll()).thenReturn(vacancies);
        doNothing().when(csvUtil).toCsvBytes(vacancies, "export.csv");

        String result = vacancyService.export("csv", "export");
        assertEquals("Export completed: export.csv", result);
        verify(csvUtil, times(1)).toCsvBytes(vacancies, "export.csv");
    }

    @Test
    void testExport_Json() throws IOException {
        List<VacancyEntity> vacancies = Collections.singletonList(VacancyEntity.builder().id("1").build());
        when(vacancyRepository.findAll()).thenReturn(vacancies);
        doNothing().when(jsonUtil).toJsonBytes(vacancies, "export.json");

        String result = vacancyService.export("json", "export");
        assertEquals("Export completed: export.json", result);
        verify(jsonUtil, times(1)).toJsonBytes(vacancies, "export.json");
    }

    @Test
    void testExport_Xlsx() throws IOException {
        List<VacancyEntity> vacancies = Collections.singletonList(VacancyEntity.builder().id("1").build());
        when(vacancyRepository.findAll()).thenReturn(vacancies);
        doNothing().when(xlsxUtil).toXlsxBytes(vacancies, "export.xlsx");

        String result = vacancyService.export("xlsx", "export");
        assertEquals("Export completed: export.xlsx", result);
        verify(xlsxUtil, times(1)).toXlsxBytes(vacancies, "export.xlsx");
    }

    @Test
    void testExport_InvalidType() {
        String result = vacancyService.export("txt", "export");
        assertEquals("No such type", result);
    }

    @Test
    void testExport_Exception() throws IOException {
        List<VacancyEntity> vacancies = Collections.singletonList(VacancyEntity.builder().id("1").build());
        when(vacancyRepository.findAll()).thenReturn(vacancies);
        doThrow(new IOException("Export error"))
                .when(csvUtil).toCsvBytes(vacancies, "export.csv");

        String result = vacancyService.export("csv", "export");
        assertTrue(result.contains("Error during export: Export error"));
    }

    @Test
    void testSendNotification() {
        VacancyEntity vacancyEntity = VacancyEntity.builder().id("1").build();
        Page<VacancyEntity> initialPage = new PageImpl<>(Collections.singletonList(vacancyEntity));
        when(vacancyRepository.search(any(), any(), any(), any(), any(), any(Pageable.class)))
                .thenReturn(initialPage);
        when(formatterService.formatResult(initialPage)).thenReturn("formatted result");

        AtomicReference<String> ar = new AtomicReference<>("testNotification");
        when(notificationService.startNotification(eq(initialPage), eq(initialPage.getContent().size())))
                .thenReturn(ar);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(baos));

        String result = vacancyService.sendNotification("Java", "Company", 1000, 2000, "Area");

        System.setOut(originalOut);

        assertEquals(String.valueOf(ar), result);
        assertTrue(baos.toString().contains("These vacancies are already in the database:\nformatted result"));

        verify(vacancyRepository, times(1)).search(any(), any(), any(), any(), any(), any(Pageable.class));
        verify(formatterService, times(1)).formatResult(initialPage);
        verify(notificationService)
                .startNotification(initialPage, initialPage.getContent().size());
    }
}