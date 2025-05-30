package com.fedordemin.vacancyparser.services;

import com.fedordemin.vacancyparser.entities.LogEntity;
import com.fedordemin.vacancyparser.entities.VacancyEntity;
import com.fedordemin.vacancyparser.repositories.VacancyRepo;
import com.fedordemin.vacancyparser.utils.strategies.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.Optional;

@Service
public class VacancyService {
    private final VacancyRepo vacancyRepository;
    private final VacancyFetcherService vacancyFetcherService;
    private final FormatterService formatterService;
    private final HistoryWriterService historyWriterService;
    private final NotificationService notificationService;

    private final Map<String, ExportStrategy> exportStrategies;

    @Autowired
    public VacancyService(VacancyRepo vacancyRepository, VacancyFetcherService vacancyFetcherService,
                          FormatterService formatterService, HistoryWriterService historyWriterService,
                          NotificationService notificationService, CsvExportStrategy csvStrategy,
                          JsonExportStrategy jsonStrategy, XlsxExportStrategy xlsxStrategy) {
        this.vacancyRepository = vacancyRepository;
        this.vacancyFetcherService = vacancyFetcherService;
        this.formatterService = formatterService;
        this.historyWriterService = historyWriterService;
        this.notificationService = notificationService;

        this.exportStrategies = Map.of(
                "csv", csvStrategy,
                "json", jsonStrategy,
                "xlsx", xlsxStrategy
        );
    }

    @Value("${app.pagination.default-size:10}")
    private int defaultPageSize;

    public Page<VacancyEntity> getVacancies(
            String title,
            String company,
            Integer minSalary,
            Integer maxSalary,
            String area,
            int page,
            int size
    ) {
        int pageSize = size > 0 ? size : defaultPageSize;
        return vacancyRepository.search(
                title,
                company,
                minSalary,
                maxSalary,
                area,
                PageRequest.of(page, pageSize, Sort.by("published_at").descending())
        );
    }

    public VacancyEntity getVacancy(String id) {
        return vacancyRepository.findById(id).orElse(null);
    }

    public List<VacancyEntity> getAllVacancies() {
        return vacancyRepository.findAll();
    }

    public boolean deleteVacancy(String id) {
        Optional<VacancyEntity> vacancyOpt = vacancyRepository.findById(id);
        if (vacancyOpt.isPresent()) {
            LogEntity logEntity = LogEntity.builder()
                    .vacancyId(id)
                    .type("deleted")
                    .isByUser(true)
                    .timestamp(LocalDateTime.now())
                    .build();
            historyWriterService.write(logEntity);
            vacancyRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Transactional
    public void fetchVacancies(String searchText, String company_name, String area, String site) {
        vacancyFetcherService.fetchVacancies(searchText, company_name, area, site, true);
    }

    public String formatResult(Page<VacancyEntity> page) {
        return formatterService.formatResult(page);
    }

    public String formatVacancy(VacancyEntity vacancy) {
        return formatterService.formatVacancy(vacancy);
    }

    public String formatHistory(String type) {
        return formatterService.formatHistory(historyWriterService.getAllLogs(), type);
    }

    public String export(String fileType, String filename) {
        try {
            String fullFilename = filename + "." + fileType;
            ExportStrategy strategy = exportStrategies.get(fileType.toLowerCase());
            if (strategy == null) {
                return "Неподдерживаемый тип файла";
            }
            List<VacancyEntity> all = getAllVacancies();
            strategy.export(all, fullFilename);
            return "Экспорт завершен: " + fullFilename;
        } catch (IOException e) {
            return "Ошибка при экспорте: " + e.getMessage();
        }
    }

    public String sendNotification(String title, String company, Integer minSalary, Integer maxSalary, String area) {
        int size = 5;
        Page<VacancyEntity> initialPage = getVacancies(title, company, minSalary, maxSalary, area, 0, size);
        String output = formatResult(initialPage);
        System.out.println("These vacancies are already in the database:\n" + output);

        return String.valueOf(notificationService.startNotification(initialPage, initialPage.getContent().size()));
    }
}
