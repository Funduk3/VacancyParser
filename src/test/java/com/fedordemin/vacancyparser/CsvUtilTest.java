package com.fedordemin.vacancyparser;

import com.fedordemin.vacancyparser.entities.VacancyEntity;
import com.fedordemin.vacancyparser.utils.CsvUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CsvUtilTest {

    private final CsvUtil csvUtil = new CsvUtil();

    @Test
    public void testToCsvBytesWithEmptyList(@TempDir Path tempDir) throws IOException {
        Path tempFile = tempDir.resolve("empty.csv");
        csvUtil.toCsvBytes(Collections.emptyList(), tempFile.toString());
        String content = Files.readString(tempFile);

        String trimmedContent = content.trim();
        String expectedHeader = String.join(",", "ID", "Name", "SalaryFrom", "SalaryTo", "Employer",
                "Description", "City", "Published at", "Requirements", "Schedule", "Experience", "Url");
        String firstLine = trimmedContent.split("\\R")[0];
        assertEquals(expectedHeader, firstLine);

        String[] lines = trimmedContent.split("\\R");
        assertEquals(1, lines.length);
    }

    @Test
    public void testToCsvBytesWithOneRecord(@TempDir Path tempDir) throws IOException {
        Path tempFile = tempDir.resolve("oneRecord.csv");

        VacancyEntity vacancy = Mockito.mock(VacancyEntity.class);
        Mockito.when(vacancy.getId()).thenReturn("1");
        Mockito.when(vacancy.getName()).thenReturn("Java Developer");
        Mockito.when(vacancy.getSalaryFrom()).thenReturn(Integer.valueOf("1000"));
        Mockito.when(vacancy.getSalaryTo()).thenReturn(Integer.valueOf("2000"));
        Mockito.when(vacancy.getEmployerName()).thenReturn("TestCompany");
        Mockito.when(vacancy.getDescription()).thenReturn("Job description");
        Mockito.when(vacancy.getCity()).thenReturn("Moscow");
        Mockito.when(vacancy.getPublished_at()).thenReturn(LocalDateTime.parse("2021-09-01T00:00"));
        Mockito.when(vacancy.getRequirements()).thenReturn("Requirements info");
        Mockito.when(vacancy.getScheduleName()).thenReturn("Full time");
        Mockito.when(vacancy.getExperienceName()).thenReturn("Mid");
        Mockito.when(vacancy.getAlternate_url()).thenReturn("http://example.com");

        csvUtil.toCsvBytes(List.of(vacancy), tempFile.toString());
        List<String> lines = Files.readAllLines(tempFile);

        String expectedHeader = String.join(",", "ID", "Name", "SalaryFrom", "SalaryTo", "Employer",
                "Description", "City", "Published at", "Requirements", "Schedule", "Experience", "Url");
        assertEquals(expectedHeader, lines.get(0));

        String expectedRecord = String.join(",", "1", "Java Developer", "1000", "2000", "TestCompany",
                "Job description", "Moscow", "2021-09-01T00:00", "Requirements info", "Full time", "Mid", "http://example.com");
        assertEquals(expectedRecord, lines.get(1));

        assertEquals(2, lines.size());
    }

    @Test
    public void testToCsvBytesWithMultipleRecords(@TempDir Path tempDir) throws IOException {
        Path tempFile = tempDir.resolve("multipleRecords.csv");

        VacancyEntity vacancy1 = Mockito.mock(VacancyEntity.class);
        Mockito.when(vacancy1.getId()).thenReturn("1");
        Mockito.when(vacancy1.getName()).thenReturn("Java Developer");
        Mockito.when(vacancy1.getSalaryFrom()).thenReturn(Integer.valueOf("1000"));
        Mockito.when(vacancy1.getSalaryTo()).thenReturn(Integer.valueOf("2000"));
        Mockito.when(vacancy1.getEmployerName()).thenReturn("TestCompany1");
        Mockito.when(vacancy1.getDescription()).thenReturn("Job description 1");
        Mockito.when(vacancy1.getCity()).thenReturn("Moscow");
        Mockito.when(vacancy1.getPublished_at()).thenReturn(LocalDateTime.parse("2021-09-01T00:00"));
        Mockito.when(vacancy1.getRequirements()).thenReturn("Requirements 1");
        Mockito.when(vacancy1.getScheduleName()).thenReturn("Full time");
        Mockito.when(vacancy1.getExperienceName()).thenReturn("Mid");
        Mockito.when(vacancy1.getAlternate_url()).thenReturn("http://example.com/1");

        VacancyEntity vacancy2 = Mockito.mock(VacancyEntity.class);
        Mockito.when(vacancy2.getId()).thenReturn("2");
        Mockito.when(vacancy2.getName()).thenReturn("Senior Java Developer");
        Mockito.when(vacancy2.getSalaryFrom()).thenReturn(Integer.valueOf("3000"));
        Mockito.when(vacancy2.getSalaryTo()).thenReturn(Integer.valueOf("4000"));
        Mockito.when(vacancy2.getEmployerName()).thenReturn("TestCompany2");
        Mockito.when(vacancy2.getDescription()).thenReturn("Job description 2");
        Mockito.when(vacancy2.getCity()).thenReturn("Saint Petersburg");
        Mockito.when(vacancy2.getPublished_at()).thenReturn(LocalDateTime.parse("2021-10-01T00:00"));
        Mockito.when(vacancy2.getRequirements()).thenReturn("Requirements 2");
        Mockito.when(vacancy2.getScheduleName()).thenReturn("Part time");
        Mockito.when(vacancy2.getExperienceName()).thenReturn("Senior");
        Mockito.when(vacancy2.getAlternate_url()).thenReturn("http://example.com/2");

        csvUtil.toCsvBytes(List.of(vacancy1, vacancy2), tempFile.toString());
        List<String> lines = Files.readAllLines(tempFile);

        String expectedHeader = String.join(",", "ID", "Name", "SalaryFrom", "SalaryTo", "Employer",
                "Description", "City", "Published at", "Requirements", "Schedule", "Experience", "Url");
        assertEquals(expectedHeader, lines.get(0));
        assertEquals(3, lines.size());

        String expectedRecord1 = String.join(",", "1", "Java Developer", "1000", "2000", "TestCompany1",
                "Job description 1", "Moscow", "2021-09-01T00:00", "Requirements 1", "Full time", "Mid", "http://example.com/1");
        String expectedRecord2 = String.join(",", "2", "Senior Java Developer", "3000", "4000", "TestCompany2",
                "Job description 2", "Saint Petersburg", "2021-10-01T00:00", "Requirements 2", "Part time", "Senior", "http://example.com/2");
        assertTrue(lines.contains(expectedRecord1));
        assertTrue(lines.contains(expectedRecord2));
    }
}