package com.fedordemin.vacancyparser.services.secondLayer;

import com.fedordemin.vacancyparser.entities.LogEntity;
import com.fedordemin.vacancyparser.entities.VacancyEntity;
import com.fedordemin.vacancyparser.repositories.VacancyRepo;
import com.fedordemin.vacancyparser.services.HistoryWriterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class VacancyManagementService {
    private final VacancyRepo vacancyRepository;
    private final HistoryWriterService historyWriterService;

    @Autowired
    public VacancyManagementService(VacancyRepo vacancyRepository, HistoryWriterService historyWriterService) {
        this.vacancyRepository = vacancyRepository;
        this.historyWriterService = historyWriterService;
    }

    @Value("${app.pagination.default-size:10}")
    private int defaultPageSize;

    public Page<VacancyEntity> getVacancies(String title, String company,
                                            Integer minSalary, Integer maxSalary, String area, int page, int size) {
        int pageSize = size > 0 ? size : defaultPageSize;
        return vacancyRepository.search(title, company, minSalary, maxSalary, area,
                PageRequest.of(page, pageSize, Sort.by("published_at").descending()));
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
}
