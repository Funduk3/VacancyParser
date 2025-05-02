package com.fedordemin.vacancyparser.services;

import com.fedordemin.vacancyparser.models.*;
import com.fedordemin.vacancyparser.models.entities.VacancyEntity;
import com.fedordemin.vacancyparser.repositories.VacancyRepo;
import lombok.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@EnableScheduling
public class VacancySchedulerService {
    private final HHApiService hhApiService;
    private final VacancyRepo vacancyRepository;

    @Value("${app.schedule.cron:0 */30 * * * *}")
    @Scheduled(cron = "${app.schedule.cron}")
    public void fetchAndSaveVacancies() {
        VacancyResponse response = hhApiService.searchVacancies("Java", "1", 0, 100);

        response.getItems().forEach(vacancy -> {
            if (!vacancyRepository.existsById(vacancy.getId())) {
                VacancyEntity entity = convertToEntity(vacancy);
                vacancyRepository.save(entity);
            }
        });
    }

    private VacancyEntity convertToEntity(Vacancy vacancy) {
        return new VacancyEntity(
                vacancy.getId(),
                vacancy.getName(),
                vacancy.getEmployer().getName(),
                vacancy.getSalary() != null ? vacancy.getSalary().getFrom() : null,
                vacancy.getSalary() != null ? vacancy.getSalary().getTo() : null,
                vacancy.getSalary() != null ? vacancy.getSalary().getCurrency() : null,
                vacancy.getPublishedAt(),
                vacancy.getDescription()
        );
    }
}
