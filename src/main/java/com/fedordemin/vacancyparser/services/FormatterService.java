package com.fedordemin.vacancyparser.services;

import com.fedordemin.vacancyparser.entities.LogEntity;
import com.fedordemin.vacancyparser.entities.VacancyEntity;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

@Component
public class FormatterService {
    public String formatResult(Page<VacancyEntity> page) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("\n=== Page %d/%d (%d total) ===\n",
                page.getNumber() + 1,
                page.getTotalPages(),
                page.getTotalElements()));

        page.getContent().forEach(v -> sb.append(formatVacancy(v)));

        return sb.toString();
    }

    public String formatVacancy(VacancyEntity vacancy) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("\nID: %s\nTitle: %s\nCompany: %s\n",
                vacancy.getId(), vacancy.getName(), vacancy.getEmployerName()));

        if (vacancy.getSalaryFrom() != null || vacancy.getSalaryTo() != null) {
            sb.append(String.format("Salary: %s - %s %s\n",
                    vacancy.getSalaryFrom() != null ? vacancy.getSalaryFrom() : "?",
                    vacancy.getSalaryTo() != null ? vacancy.getSalaryTo() : "?",
                    vacancy.getSalaryCurrency() != null ? vacancy.getSalaryCurrency() : "RUB"));
        }
        sb.append(String.format("Requirements: %s\n",
                vacancy.getRequirements() != null ? vacancy.getRequirements() : "N/A"));

        sb.append(String.format("Published: %s\n",
                vacancy.getPublishedAt() != null ?
                        vacancy.getPublishedAt().format(DateTimeFormatter.ISO_LOCAL_DATE) :
                        "N/A"
        ));
        sb.append(String.format("Url: %s\n",
                vacancy.getAlternate_url() != null ? vacancy.getAlternate_url() : "N/A"));

        return sb.toString();
    }

    public String formatLog(LogEntity logEntity, String type) {
        if (logEntity.getIsByUser()) {
            StringBuilder sb = new StringBuilder();
            sb.append("-----===-----\n");
            sb.append(String.format("Vacancy: %s\n", logEntity.getVacancyId()));
            if (Objects.equals(type, "all")) {
                sb.append(String.format("Action: %s\n", logEntity.getType()));
                sb.append(String.format("Date of action: %s\n", logEntity.getTimestamp()));
                return sb.toString();
            } else if (Objects.equals(type.toLowerCase(), logEntity.getType())) {
                sb.append(String.format("Action: %s\n", logEntity.getType()));
                sb.append(String.format("Date of action: %s\n", logEntity.getTimestamp()));
                return sb.toString();
            }
        }
        return "";
    }

    public String formatHistory(List<LogEntity> list, String type) {
        StringBuilder sb = new StringBuilder();
        list.forEach(v -> sb.append(formatLog(v, type)));
        return sb.toString();
    }
}