package com.fedordemin.vacancyparser.utils.strategies;

import com.fedordemin.vacancyparser.entities.VacancyEntity;
import com.fedordemin.vacancyparser.utils.JsonExportUtil;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class JsonExportStrategy implements ExportStrategy {
    private final JsonExportUtil jsonUtil;

    public JsonExportStrategy(JsonExportUtil jsonUtil) {
        this.jsonUtil = jsonUtil;
    }

    @Override
    public void export(List<VacancyEntity> data, String filename) throws IOException {
        jsonUtil.export(data, filename);
    }
}
