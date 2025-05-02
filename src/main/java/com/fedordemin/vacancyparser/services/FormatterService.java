package com.fedordemin.vacancyparser.services;

import com.fedordemin.vacancyparser.models.Vacancy;
import com.fedordemin.vacancyparser.models.VacancyResponse;
import com.fedordemin.vacancyparser.models.entities.VacancyEntity;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
public class FormatterService {
    public String formatResult(Page<VacancyEntity> page) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("\n=== Page %d/%d (%d total) ===\n",
                page.getNumber() + 1,
                page.getTotalPages(),
                page.getTotalElements()));

        page.getContent().forEach(v -> {
            sb.append(String.format("\nID: %s\nTitle: %s\nCompany: %s\n",
                    v.getId(), v.getName(), v.getEmployerName()));

            if (v.getSalaryFrom() != null || v.getSalaryTo() != null) {
                sb.append(String.format("Salary: %s - %s\n",
                        v.getSalaryFrom() != null ? v.getSalaryFrom() : "?",
                        v.getSalaryTo() != null ? v.getSalaryTo() : "?"));
            }

            sb.append(String.format("Published: %s\n",
                    v.getPublishedAt() != null ?
                            v.getPublishedAt().format(DateTimeFormatter.ISO_LOCAL_DATE) :
                            "N/A"
            ));
        });

        return sb.toString();
    }

    public String formatResponse(VacancyResponse response) {
        if (response == null || response.getItems() == null) {
            return "No vacancies found";
        }

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Found %d vacancies (Page %d/%d):\n\n",
                response.getFound(), response.getPage() + 1, response.getPages()));

        for (Vacancy vacancy : response.getItems()) {
            sb.append(String.format("➤ %s\n", vacancy.getName()))
                    .append(String.format("   ID: %s\n", vacancy.getId()))
                    .append(String.format("   Company: %s\n",
                            vacancy.getEmployer() != null ? vacancy.getEmployer().getName() : "Unknown"))
                    .append(formatSalary(vacancy.getSalary()))
                    .append(String.format("   Published: %s\n\n",
                            vacancy.getPublishedAt() != null ?
                                    vacancy.getPublishedAt().format(DateTimeFormatter.ISO_LOCAL_DATE) :
                                    "Unknown"));
        }
        return sb.toString();
    }

    public String formatSalary(Vacancy.Salary salary) {
        if (salary == null) return "   Salary: Not specified\n";
        return String.format("   Salary: %s - %s %s\n",
                salary.getFrom() != null ? salary.getFrom() : "?",
                salary.getTo() != null ? salary.getTo() : "?",
                salary.getCurrency() != null ? salary.getCurrency() : "?");
    }
}
