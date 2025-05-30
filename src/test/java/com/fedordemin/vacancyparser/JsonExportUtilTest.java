package com.fedordemin.vacancyparser;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fedordemin.vacancyparser.entities.VacancyEntity;
import com.fedordemin.vacancyparser.utils.JsonExportUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JsonExportUtilTest {

    private final JsonExportUtil jsonUtil = new JsonExportUtil();
    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @Test
    public void testToJsonBytesWithEmptyList(@TempDir Path tempDir) throws IOException {
        Path tempFile = tempDir.resolve("empty.json");
        jsonUtil.export(List.of(), tempFile.toString());
        String content = Files.readString(tempFile).trim();
        assertEquals("[ ]", content);
    }

    @Test
    public void testToJsonBytesWithOneRecord(@TempDir Path tempDir) throws IOException {
        Path tempFile = tempDir.resolve("oneRecord.json");

        VacancyEntity vacancy = VacancyEntity.builder()
                .id("1")
                .name("Java Developer")
                .salaryFrom(1000)
                .salaryTo(2000)
                .employerName("TestCompany")
                .description("Job description")
                .city("Moscow")
                .published_at(LocalDateTime.parse("2021-09-01T00:00"))
                .requirements("Requirements info")
                .scheduleName("Full time")
                .experienceName("Mid")
                .alternate_url("http://example.com")
                .build();

        jsonUtil.export(List.of(vacancy), tempFile.toString());
        String content = Files.readString(tempFile);
        List<VacancyEntity> result = mapper.readValue(content, new TypeReference<List<VacancyEntity>>() {});

        assertEquals(1, result.size());
        VacancyEntity resultVacancy = result.get(0);
        assertEquals("1", resultVacancy.getId());
        assertEquals("Java Developer", resultVacancy.getName());
        assertEquals(1000, resultVacancy.getSalaryFrom());
        assertEquals(2000, resultVacancy.getSalaryTo());
        assertEquals("TestCompany", resultVacancy.getEmployerName());
        assertEquals("Job description", resultVacancy.getDescription());
        assertEquals("Moscow", resultVacancy.getCity());
        assertEquals(LocalDateTime.parse("2021-09-01T00:00"), resultVacancy.getPublished_at());
        assertEquals("Requirements info", resultVacancy.getRequirements());
        assertEquals("Full time", resultVacancy.getScheduleName());
        assertEquals("Mid", resultVacancy.getExperienceName());
        assertEquals("http://example.com", resultVacancy.getAlternate_url());
    }

    @Test
    public void testToJsonBytesWithMultipleRecords(@TempDir Path tempDir) throws IOException {
        Path tempFile = tempDir.resolve("multipleRecords.json");

        VacancyEntity vacancy1 = VacancyEntity.builder()
                .id("1")
                .name("Java Developer")
                .salaryFrom(1000)
                .salaryTo(2000)
                .employerName("TestCompany1")
                .description("Job description 1")
                .city("Moscow")
                .published_at(LocalDateTime.parse("2021-09-01T00:00:00"))
                .requirements("Requirements 1")
                .scheduleName("Full time")
                .experienceName("Mid")
                .alternate_url("http://example.com/1")
                .build();

        VacancyEntity vacancy2 = VacancyEntity.builder()
                .id("2")
                .name("Senior Java Developer")
                .salaryFrom(3000)
                .salaryTo(4000)
                .employerName("TestCompany2")
                .description("Job description 2")
                .city("Saint Petersburg")
                .published_at(LocalDateTime.parse("2021-10-01T00:00:00"))
                .requirements("Requirements 2")
                .scheduleName("Part time")
                .experienceName("Senior")
                .alternate_url("http://example.com/2")
                .build();

        jsonUtil.export(List.of(vacancy1, vacancy2), tempFile.toString());
        String content = Files.readString(tempFile);
        List<VacancyEntity> result = mapper.readValue(content, new TypeReference<List<VacancyEntity>>() {});

        assertEquals(2, result.size());
        boolean containsVacancy1 = result.stream().anyMatch(v -> v.getId().equals("1") &&
                v.getName().equals("Java Developer") &&
                v.getEmployerName().equals("TestCompany1"));
        boolean containsVacancy2 = result.stream().anyMatch(v -> v.getId().equals("2") &&
                v.getName().equals("Senior Java Developer") &&
                v.getEmployerName().equals("TestCompany2"));
        assertTrue(containsVacancy1);
        assertTrue(containsVacancy2);
    }
}