package com.fedordemin.vacancyparser.utils;

import com.fedordemin.vacancyparser.entities.VacancyEntity;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@Component
public class XlsxUtil {
    private static Class<?> clazz = VacancyEntity.class;

    public void toXlsxBytes(List<?> list, String filename) throws IOException {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Sheet1");
            Field[] fields = clazz.getDeclaredFields();

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < fields.length; i++) {
                fields[i].setAccessible(true);
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(fields[i].getName());
            }

            for (int r = 0; r < list.size(); r++) {
                Object obj = list.get(r);
                Row row = sheet.createRow(r + 1);
                for (int c = 0; c < fields.length; c++) {
                    Cell cell = row.createCell(c);
                    Object value = fields[c].get(obj);
                    if (value != null) {
                        cell.setCellValue(value.toString());
                    }
                }
            }
            workbook.write(out);
            Files.write(Paths.get(filename), out.toByteArray());
        } catch (IllegalAccessException e) {
            throw new IOException("Ошибка рефлексии при формировании XLSX", e);
        }
    }
}

