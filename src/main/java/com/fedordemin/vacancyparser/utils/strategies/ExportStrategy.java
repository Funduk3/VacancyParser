package com.fedordemin.vacancyparser.utils.strategies;

import com.fedordemin.vacancyparser.entities.VacancyEntity;

import java.io.IOException;
import java.util.List;

public interface ExportStrategy {
    void export(List<VacancyEntity> data, String filename) throws IOException;
}
