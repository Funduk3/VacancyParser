package com.fedordemin.vacancyparser.services;

import com.fedordemin.vacancyparser.models.entities.*;
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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Service
public class VacancyService {
    private final VacancyRepo repository;
    private final VacancyFetcherService vacancyFetcherService;
    private final FormatterService formatterService;

    @Autowired
    public VacancyService(VacancyRepo repository, VacancyFetcherService vacancyFetcherService,
                          FormatterService formatterService) {
        this.repository = repository;
        this.vacancyFetcherService = vacancyFetcherService;
        this.formatterService = formatterService;
    }

    @Value("${app.pagination.default-size:10}")
    private int defaultPageSize;

    public Page<VacancyEntity> getVacancies(
            String title,
            String company,
            Integer minSalary,
            Integer maxSalary,
            int page,
            int size
    ) {
        int pageSize = size > 0 ? size : defaultPageSize;
        return repository.search(
                title,
                company,
                minSalary,
                maxSalary,
                PageRequest.of(page, pageSize, Sort.by("publishedAt").descending())
        );
    }

    public VacancyEntity getVacancy(String id) {
        return repository.findById(id).orElse(null);
    }

    public List<VacancyEntity> getAllVacancies() {
        return repository.findAll();
    }

    public boolean deleteVacancy(String id) {
        Optional<VacancyEntity> vacancyOpt = repository.findById(id);
        if (vacancyOpt.isPresent()) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }

    @Transactional
    public void fetchVacancies(String searchText, String area, String site) {
        vacancyFetcherService.fetchVacancies(searchText, area, site);
    }

    public String formatResult(Page<VacancyEntity> page) {
        return formatterService.formatResult(page);
    }

    public String formatVacancy(VacancyEntity vacancy) {
        return formatterService.formatVacancy(vacancy);
    }

    public String export(String fileType, String filename) {
        try {
            filename += "." + fileType;
            List<VacancyEntity> all = getAllVacancies();
            switch (fileType.toLowerCase()) {
                case "csv" -> {
                    byte[] csv = CsvUtil.toCsvBytes(all);
                    Files.write(Paths.get(filename), csv);
                    break;
                }
                case "json" -> {
                    byte[] json = JsonUtil.toJsonBytes(all);
                    Files.write(Paths.get(filename), json);
                    break;
                }
                case "xlsx" -> {
                    byte[] xlsx = XlsxUtil.toXlsxBytes(all);
                    Files.write(Paths.get(filename), xlsx);
                    break;
                }
                default -> {
                    return "No such type";
                }
            }
            return "Export completed: " + filename;
        } catch (IOException e) {
            return "Error during export: " + e.getMessage();
        }
    }
}