package com.fedordemin.vacancyparser.utils;

import com.fedordemin.vacancyparser.models.entities.VacancyEntity;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class CsvUtil {
    public static byte[] toCsvBytes(List<VacancyEntity> list) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (CSVPrinter printer = new CSVPrinter(
                new OutputStreamWriter(out, StandardCharsets.UTF_8),
                CSVFormat.DEFAULT.withHeader("ID","Name","SalaryFrom","SalaryTo", "Employer",
                        "Description", "City", "Published at", "Requirements", "Schedule",
                        "Experience", "Url"))) {
            for (VacancyEntity v : list) {
                printer.printRecord(v.getId(), v.getName(), v.getSalaryFrom(), v.getSalaryTo(),
                        v.getEmployerName(), v.getDescription(), v.getCity(), v.getPublishedAt(),
                        v.getRequirements(), v.getScheduleName(), v.getExperienceName(),
                        v.getAlternate_url());
            }
        }
        return out.toByteArray();
    }
}

