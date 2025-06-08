package com.fedordemin.vacancyparser;

import com.fedordemin.vacancyparser.entities.VacancyEntity;
import com.fedordemin.vacancyparser.services.NotificationService;
import com.fedordemin.vacancyparser.services.secondLayer.VacancyManagementService;
import com.fedordemin.vacancyparser.services.secondLayer.VacancyNotificationService;
import com.fedordemin.vacancyparser.utils.format.VacancyFormatterUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VacancyNotificationServiceTest {

    @Mock
    private VacancyManagementService vacancyManagementService;

    @Mock
    private NotificationService notificationService;

    @Mock
    private VacancyFormatterUtil vacancyFormatterUtil;

    private VacancyNotificationService vacancyNotificationService;
    private PrintStream originalOut;
    private ByteArrayOutputStream outputStream;

    @BeforeEach
    void setUp() {
        vacancyNotificationService = new VacancyNotificationService(
                vacancyManagementService,
                notificationService,
                vacancyFormatterUtil
        );
        outputStream = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(outputStream));
    }

    @Test
    void testSendNotification() {
        VacancyEntity vacancy = VacancyEntity.builder()
                .id("1")
                .name("Java Developer")
                .build();
        Page<VacancyEntity> page = new PageImpl<>(Collections.singletonList(vacancy));
        String formattedResult = "Formatted vacancy result";

        when(vacancyManagementService.getVacancies(
                anyString(), anyString(), anyInt(), anyInt(), anyString(), anyInt(), anyInt()))
                .thenReturn(page);
        when(vacancyFormatterUtil.formatResult(page, "default")).thenReturn(formattedResult);
        when(notificationService.startNotification(page, 1)).thenReturn(new AtomicReference<>("true"));

        String result = vacancyNotificationService.sendNotification(
                "Java", "Company", 1000, 2000, "Moscow");

        assertEquals("true", result);
        verify(vacancyManagementService).getVacancies(
                "Java", "Company", 1000, 2000, "Moscow", 0, 5);
        verify(vacancyFormatterUtil).formatResult(page, "default");
        verify(notificationService).startNotification(page, 1);

        String consoleOutput = outputStream.toString();
        assertEquals("These vacancies are already in the database:\n" + formattedResult,
                consoleOutput.trim());
    }

    @Test
    void testSendNotificationWithEmptyResult() {
        Page<VacancyEntity> emptyPage = new PageImpl<>(Collections.emptyList());
        String formattedResult = "No vacancies found";

        when(vacancyManagementService.getVacancies(
                anyString(), anyString(), any(), any(), anyString(), anyInt(), anyInt()))
                .thenReturn(emptyPage);
        when(vacancyFormatterUtil.formatResult(emptyPage, "default")).thenReturn(formattedResult);
        when(notificationService.startNotification(emptyPage, 0)).thenReturn(new AtomicReference<>("false"));

        String result = vacancyNotificationService.sendNotification(
                "Python", "Company", null, null, "Area");

        assertEquals("false", result);
        verify(vacancyManagementService).getVacancies(
                "Python", "Company", null, null, "Area", 0, 5);
        verify(vacancyFormatterUtil).formatResult(emptyPage, "default");
        verify(notificationService).startNotification(emptyPage, 0);
    }
}