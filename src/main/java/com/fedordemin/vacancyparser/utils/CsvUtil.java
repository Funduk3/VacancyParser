package com.fedordemin.vacancyparser.utils;

import com.fedordemin.vacancyparser.entities.VacancyEntity;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@Component
public class CsvUtil {
    public void toCsvBytes(List<VacancyEntity> list, String filename) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (CSVPrinter printer = new CSVPrinter(
                new OutputStreamWriter(out, StandardCharsets.UTF_8),
                CSVFormat.DEFAULT.withHeader("ID","Name","SalaryFrom","SalaryTo", "Employer",
                        "Description", "City", "Published at", "Requirements", "Schedule",
                        "Experience", "Url"))) {
            for (VacancyEntity v : list) {
                printer.printRecord(v.getId(), v.getName(), v.getSalaryFrom(), v.getSalaryTo(),
                        v.getEmployerName(), v.getDescription(), v.getCity(), v.getPublished_at(),
                        v.getRequirements(), v.getScheduleName(), v.getExperienceName(),
                        v.getAlternate_url());
            }
        }
        Files.write(Paths.get(filename), out.toByteArray());
    }
}

