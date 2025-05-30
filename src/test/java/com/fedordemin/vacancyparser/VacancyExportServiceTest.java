package com.fedordemin.vacancyparser;

import com.fedordemin.vacancyparser.entities.VacancyEntity;
import com.fedordemin.vacancyparser.services.secondLayer.VacancyExportService;
import com.fedordemin.vacancyparser.services.secondLayer.VacancyManagementService;
import com.fedordemin.vacancyparser.utils.export.CsvExportUtil;
import com.fedordemin.vacancyparser.utils.export.ExportUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VacancyExportServiceTest {

    @Mock
    private VacancyManagementService vacancyManagementService;

    @Mock
    private CsvExportUtil csvExportUtil;

    private Map<String, ExportUtil> exportStrategies;

    private VacancyExportService vacancyExportService;

    @BeforeEach
    void setUp() {
        exportStrategies = new HashMap<>();
        exportStrategies.put("csv", csvExportUtil);
        vacancyExportService = new VacancyExportService(vacancyManagementService, exportStrategies);
    }

    @Test
    void testExportSuccess() throws IOException {
        List<VacancyEntity> vacancies = Collections.singletonList(new VacancyEntity());
        when(vacancyManagementService.getAllVacancies()).thenReturn(vacancies);
        doNothing().when(csvExportUtil).export(vacancies, "test.csv");

        String result = vacancyExportService.export("csv", "test");

        assertEquals("Экспорт завершен: test.csv", result);
        verify(vacancyManagementService).getAllVacancies();
        verify(csvExportUtil).export(vacancies, "test.csv");
    }

    @Test
    void testExportUnsupportedType() {
        String result = vacancyExportService.export("pdf", "test");

        assertEquals("Неподдерживаемый тип файла", result);
        verify(vacancyManagementService, never()).getAllVacancies();
    }

    @Test
    void testExportIOException() throws IOException {
        List<VacancyEntity> vacancies = Collections.singletonList(new VacancyEntity());
        when(vacancyManagementService.getAllVacancies()).thenReturn(vacancies);
        doThrow(new IOException("Тестовая ошибка"))
                .when(csvExportUtil).export(vacancies, "test.csv");

        String result = vacancyExportService.export("csv", "test");

        assertEquals("Ошибка при экспорте: Тестовая ошибка", result);
        verify(vacancyManagementService).getAllVacancies();
        verify(csvExportUtil).export(vacancies, "test.csv");
    }
}