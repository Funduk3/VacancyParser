package com.fedordemin.vacancyparser;

import com.fedordemin.vacancyparser.entities.LogEntity;
import com.fedordemin.vacancyparser.entities.VacancyEntity;
import com.fedordemin.vacancyparser.repositories.VacancyRepo;
import com.fedordemin.vacancyparser.services.HistoryWriterService;
import com.fedordemin.vacancyparser.services.secondLayer.VacancyManagementService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VacancyManagementServiceTest {

    @Mock
    private VacancyRepo vacancyRepository;

    @Mock
    private HistoryWriterService historyWriterService;

    private VacancyManagementService vacancyManagementService;
    private VacancyEntity testVacancy;

    @BeforeEach
    void setUp() {
        vacancyManagementService = new VacancyManagementService(vacancyRepository, historyWriterService);
        testVacancy = VacancyEntity.builder()
                .id("1")
                .name("Java Developer")
                .build();
    }

    @Test
    void testGetVacancies() {
        Page<VacancyEntity> expectedPage = new PageImpl<>(Collections.singletonList(testVacancy));
        when(vacancyRepository.search(
                anyString(), anyString(), anyInt(), anyInt(), anyString(), any(PageRequest.class)))
                .thenReturn(expectedPage);

        Page<VacancyEntity> result = vacancyManagementService.getVacancies(
                "Java", "Company", 1000, 2000, "Moscow", 0, 10);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(testVacancy, result.getContent().get(0));

        verify(vacancyRepository).search(
                eq("Java"),
                eq("Company"),
                eq(1000),
                eq(2000),
                eq("Moscow"),
                argThat(pageRequest ->
                        pageRequest.getPageNumber() == 0 &&
                                pageRequest.getPageSize() == 10 &&
                                pageRequest.getSort().equals(Sort.by("published_at").descending())
                )
        );
    }

    @Test
    void testGetVacancy() {
        when(vacancyRepository.findById("1")).thenReturn(Optional.of(testVacancy));

        VacancyEntity result = vacancyManagementService.getVacancy("1");

        assertNotNull(result);
        assertEquals("1", result.getId());
        assertEquals("Java Developer", result.getName());
        verify(vacancyRepository).findById("1");
    }

    @Test
    void testGetVacancyNotFound() {
        when(vacancyRepository.findById("999")).thenReturn(Optional.empty());

        VacancyEntity result = vacancyManagementService.getVacancy("999");

        assertNull(result);
        verify(vacancyRepository).findById("999");
    }

    @Test
    void testGetAllVacancies() {
        List<VacancyEntity> expectedList = Collections.singletonList(testVacancy);
        when(vacancyRepository.findAll()).thenReturn(expectedList);

        List<VacancyEntity> result = vacancyManagementService.getAllVacancies();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testVacancy, result.get(0));
        verify(vacancyRepository).findAll();
    }

    @Test
    void testDeleteVacancySuccess() {
        when(vacancyRepository.findById("1")).thenReturn(Optional.of(testVacancy));
        doNothing().when(vacancyRepository).deleteById("1");

        boolean result = vacancyManagementService.deleteVacancy("1");

        assertTrue(result);

        ArgumentCaptor<LogEntity> logCaptor = ArgumentCaptor.forClass(LogEntity.class);
        verify(historyWriterService).write(logCaptor.capture());

        LogEntity capturedLog = logCaptor.getValue();
        assertEquals("1", capturedLog.getVacancyId());
        assertEquals("deleted", capturedLog.getType());
        assertTrue(capturedLog.getIsByUser());
        assertNotNull(capturedLog.getTimestamp());

        verify(vacancyRepository).deleteById("1");
    }

    @Test
    void testDeleteVacancyNotFound() {
        when(vacancyRepository.findById("999")).thenReturn(Optional.empty());

        boolean result = vacancyManagementService.deleteVacancy("999");

        assertFalse(result);
        verify(vacancyRepository, never()).deleteById(any());
        verify(historyWriterService, never()).write(any());
    }
}