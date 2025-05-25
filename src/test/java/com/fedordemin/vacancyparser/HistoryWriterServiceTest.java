package com.fedordemin.vacancyparser;

import com.fedordemin.vacancyparser.entities.LogEntity;
import com.fedordemin.vacancyparser.entities.VacancyEntity;
import com.fedordemin.vacancyparser.repositories.HistoryRepo;
import com.fedordemin.vacancyparser.services.HistoryWriterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class HistoryWriterServiceTest {

    @Mock
    private HistoryRepo historyRepo;

    @InjectMocks
    private HistoryWriterService historyWriterService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllLogs() {
        LogEntity log1 = LogEntity.builder()
                .vacancyId("1")
                .type("added")
                .timestamp(LocalDateTime.now())
                .build();
        LogEntity log2 = LogEntity.builder()
                .vacancyId("2")
                .type("added")
                .timestamp(LocalDateTime.now())
                .build();
        List<LogEntity> logs = Arrays.asList(log1, log2);
        when(historyRepo.findAll()).thenReturn(logs);

        List<LogEntity> result = historyWriterService.getAllLogs();

        verify(historyRepo, times(1)).findAll();
        assertEquals(2, result.size());
        assertEquals("1", result.get(0).getVacancyId());
        assertEquals("2", result.get(1).getVacancyId());
    }

    @Test
    void testWrite() {
        LogEntity log = LogEntity.builder()
                .vacancyId("3")
                .type("added")
                .timestamp(LocalDateTime.now())
                .build();

        historyWriterService.write(log);
        verify(historyRepo, times(1)).save(log);
    }

    @Test
    void testSaveToHistory() {
        VacancyEntity vacancy = new VacancyEntity();
        vacancy.setId("10");
        boolean isByUser = true;

        historyWriterService.saveToHistory(isByUser, vacancy);

        ArgumentCaptor<LogEntity> captor = ArgumentCaptor.forClass(LogEntity.class);
        verify(historyRepo, times(1)).save(captor.capture());
        LogEntity savedLog = captor.getValue();

        assertEquals("10", savedLog.getVacancyId());
        assertEquals("added", savedLog.getType());
        assertEquals(isByUser, savedLog.getIsByUser());
        assertNotNull(savedLog.getTimestamp());
    }
}