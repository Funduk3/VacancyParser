package com.fedordemin.vacancyparser.utils.export;

import com.fedordemin.vacancyparser.entities.VacancyEntity;

import java.io.IOException;
import java.util.List;

public interface ExportUtil {
    void export(List<VacancyEntity> list, String filename) throws IOException;
}
