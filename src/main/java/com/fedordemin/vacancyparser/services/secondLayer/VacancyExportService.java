package com.fedordemin.vacancyparser.services.secondLayer;

import com.fedordemin.vacancyparser.entities.VacancyEntity;
import com.fedordemin.vacancyparser.utils.export.ExportUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class VacancyExportService {
    private final VacancyManagementService vacancyManagementService;
    private final Map<String, ExportUtil> exportStrategies;

    @Autowired
    public VacancyExportService(VacancyManagementService vacancyManagementService,
                                Map<String, ExportUtil> exportStrategies) {
        this.vacancyManagementService = vacancyManagementService;
        this.exportStrategies = exportStrategies;
    }

    public String export(String fileType, String filename) {
        try {
            String fullFilename = filename + "." + fileType;
            ExportUtil strategy = exportStrategies.get(fileType.toLowerCase());
            if (strategy == null) {
                return "Неподдерживаемый тип файла";
            }
            List<VacancyEntity> all = vacancyManagementService.getAllVacancies();
            strategy.export(all, fullFilename);
            return "Экспорт завершен: " + fullFilename;
        } catch (IOException e) {
            throw new IllegalStateException("Ошибка при экспорте: " + e.getMessage(), e);
        }
    }
}
