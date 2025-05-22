package com.fedordemin.vacancyparser.services;

import com.fedordemin.vacancyparser.entities.LogEntity;
import com.fedordemin.vacancyparser.entities.VacancyEntity;
import com.fedordemin.vacancyparser.repositories.VacancyRepo;
import com.fedordemin.vacancyparser.utils.CsvUtil;
import com.fedordemin.vacancyparser.utils.JsonUtil;
import com.fedordemin.vacancyparser.utils.XlsxUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class VacancyService {
    private final VacancyRepo vacancyRepository;
    private final VacancyFetcherService vacancyFetcherService;
    private final FormatterService formatterService;
    private final HistoryWriterService historyWriterService;
    private final CsvUtil csvUtil;
    private final XlsxUtil xlsxUtil;
    private final JsonUtil jsonUtil;

    @Autowired
    public VacancyService(VacancyRepo vacancyRepository, VacancyFetcherService vacancyFetcherService,
                          FormatterService formatterService, HistoryWriterService historyWriterService,
                          CsvUtil csvUtil, XlsxUtil xlsxUtil, JsonUtil jsonUtil) {
        this.vacancyRepository = vacancyRepository;
        this.vacancyFetcherService = vacancyFetcherService;
        this.formatterService = formatterService;
        this.historyWriterService = historyWriterService;
        this.csvUtil = csvUtil;
        this.xlsxUtil = xlsxUtil;
        this.jsonUtil = jsonUtil;
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
            LogEntity logEntity = new LogEntity();
            logEntity.setVacancyId(id);
            logEntity.setType("deleted");
            logEntity.setTimestamp(LocalDateTime.now());
            logEntity.setIsByUser(true);
            historyWriterService.write(logEntity);
            vacancyRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Transactional
    public void fetchVacancies(String searchText, String company, String area, String site) {
        vacancyFetcherService.fetchVacancies(searchText, company, area, site, true);
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
            filename += "." + fileType;
            List<VacancyEntity> all = getAllVacancies();
            switch (fileType.toLowerCase()) {
                case "csv" -> csvUtil.toCsvBytes(all, filename);
                case "json" -> jsonUtil.toJsonBytes(all, filename);
                case "xlsx" -> xlsxUtil.toXlsxBytes(all, filename);
                default -> {return "No such type";}
            }
            return "Export completed: " + filename;
        } catch (IOException e) {
            return "Error during export: " + e.getMessage();
        }
    }
}