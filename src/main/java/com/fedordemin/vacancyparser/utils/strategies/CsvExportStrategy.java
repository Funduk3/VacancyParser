package com.fedordemin.vacancyparser.utils.strategies;

import com.fedordemin.vacancyparser.entities.VacancyEntity;
import com.fedordemin.vacancyparser.utils.CsvExportUtil;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class CsvExportStrategy implements ExportStrategy {
    private final CsvExportUtil csvUtil;

    public CsvExportStrategy(CsvExportUtil csvUtil) {
        this.csvUtil = csvUtil;
    }

    @Override
    public void export(List<VacancyEntity> data, String filename) throws IOException {
        csvUtil.export(data, filename);
    }
}
