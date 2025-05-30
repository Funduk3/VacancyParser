package com.fedordemin.vacancyparser;

import com.fedordemin.vacancyparser.entities.VacancyEntity;
import com.fedordemin.vacancyparser.utils.export.XlsxExportUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class XlsxExportUtilTest {

    private final XlsxExportUtil xlsxUtil = new XlsxExportUtil();

    private final String[] expectedHeader = {"id", "alternate_url", "name", "salaryFrom", "salaryTo", "salaryCurrency", "salaryGross", "employerId", "employerName", "description", "city", "street", "published_at", "requirements", "scheduleName", "experienceName"};

    @Test
    public void testToXlsxBytesWithEmptyList(@TempDir Path tempDir) throws IOException {
        Path tempFile = tempDir.resolve("empty.xlsx");
        xlsxUtil.export(List.of(), tempFile.toString());

        try (FileInputStream fis = new FileInputStream(tempFile.toFile());
             XSSFWorkbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(0);
            assertEquals(1, sheet.getPhysicalNumberOfRows());
            Row headerRow = sheet.getRow(0);
            for (int i = 0; i < expectedHeader.length; i++) {
                String cellValue = headerRow.getCell(i).getStringCellValue();
                assertEquals(expectedHeader[i].toLowerCase(), cellValue.toLowerCase());
            }
        }
    }

    @Test
    public void testToXlsxBytesWithOneRecord(@TempDir Path tempDir) throws IOException {
        Path tempFile = tempDir.resolve("oneRecord.xlsx");

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

        xlsxUtil.export(List.of(vacancy), tempFile.toString());

        try (FileInputStream fis = new FileInputStream(tempFile.toFile());
             XSSFWorkbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(0);
            assertEquals(2, sheet.getPhysicalNumberOfRows());

            Row headerRow = sheet.getRow(0);
            for (int i = 0; i < expectedHeader.length; i++) {
                String cellValue = headerRow.getCell(i).getStringCellValue();
                assertEquals(expectedHeader[i], cellValue);
            }
            Row dataRow = sheet.getRow(1);
            assertEquals("1", dataRow.getCell(0).getStringCellValue());
            assertEquals("http://example.com", dataRow.getCell(1).getStringCellValue());
            assertEquals("Java Developer", dataRow.getCell(2).getStringCellValue());
            assertEquals("1000", dataRow.getCell(3).toString());
            assertEquals("2000", dataRow.getCell(4).toString());
            assertEquals("", dataRow.getCell(5).getStringCellValue());
            assertEquals("", dataRow.getCell(6).getStringCellValue());
            assertEquals("", dataRow.getCell(7).getStringCellValue());
            assertEquals("TestCompany", dataRow.getCell(8).getStringCellValue());
            assertEquals("Job description", dataRow.getCell(9).getStringCellValue());
            assertEquals("Moscow", dataRow.getCell(10).getStringCellValue());
            assertEquals("", dataRow.getCell(11).getStringCellValue());
            assertEquals("2021-09-01T00:00", dataRow.getCell(12).getStringCellValue());
            assertEquals("Requirements info", dataRow.getCell(13).getStringCellValue());
            assertEquals("Full time", dataRow.getCell(14).getStringCellValue());
            assertEquals("Mid", dataRow.getCell(15).getStringCellValue());
        }
    }

    @Test
    public void testToXlsxBytesWithMultipleRecords(@TempDir Path tempDir) throws IOException {
        Path tempFile = tempDir.resolve("multipleRecords.xlsx");

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

        xlsxUtil.export(List.of(vacancy1, vacancy2), tempFile.toString());

        try (FileInputStream fis = new FileInputStream(tempFile.toFile());
             XSSFWorkbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(0);
            assertEquals(3, sheet.getPhysicalNumberOfRows());

            Row headerRow = sheet.getRow(0);
            for (int i = 0; i < expectedHeader.length; i++) {
                String cellValue = headerRow.getCell(i).getStringCellValue();
                assertEquals(expectedHeader[i], cellValue);
            }

            boolean foundVacancy1 = false;
            boolean foundVacancy2 = false;
            for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
                Row row = sheet.getRow(i);
                String id = row.getCell(0).getStringCellValue();
                if ("1".equals(id)) {
                    foundVacancy1 = true;
                    assertEquals("http://example.com/1", row.getCell(1).getStringCellValue());
                    assertEquals("Java Developer", row.getCell(2).getStringCellValue());
                    assertEquals("1000", row.getCell(3).toString());
                    assertEquals("2000", row.getCell(4).toString());
                    assertEquals("", row.getCell(5).getStringCellValue());
                    assertEquals("", row.getCell(6).getStringCellValue());
                    assertEquals("", row.getCell(7).getStringCellValue());
                    assertEquals("TestCompany1", row.getCell(8).getStringCellValue());
                } else if ("2".equals(id)) {
                    foundVacancy2 = true;
                    assertEquals("http://example.com/2", row.getCell(1).getStringCellValue());
                    assertEquals("Senior Java Developer", row.getCell(2).getStringCellValue());
                    assertEquals("3000", row.getCell(3).toString());
                    assertEquals("4000", row.getCell(4).toString());
                    assertEquals("", row.getCell(5).getStringCellValue());
                    assertEquals("", row.getCell(6).getStringCellValue());
                    assertEquals("", row.getCell(7).getStringCellValue());
                    assertEquals("TestCompany2", row.getCell(8).getStringCellValue());
                }
            }
            assertTrue(foundVacancy1);
            assertTrue(foundVacancy2);
        }
    }
}