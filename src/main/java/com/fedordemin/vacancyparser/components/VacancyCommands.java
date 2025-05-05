package com.fedordemin.vacancyparser.components;

import com.fedordemin.vacancyparser.models.entities.VacancyEntity;
import com.fedordemin.vacancyparser.repositories.VacancyRepo;
import com.fedordemin.vacancyparser.services.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.shell.standard.*;

@ShellComponent
public class VacancyCommands {
    private final HHApiService hhApiService;
    private final VacancyService vacancyService;
    private final FormatterService formatter;
    private final VacancyFetcherService vacancyFetcherService;
    private static final Logger log = LoggerFactory.getLogger(VacancyCommands.class);

    @Autowired
    public VacancyCommands(
            HHApiService hhApiService,
            VacancyService vacancyService,
            FormatterService formatter,
            VacancyFetcherService vacancyFetcherService) {
        this.hhApiService = hhApiService;
        this.vacancyService = vacancyService;
        this.formatter = formatter;
        this.vacancyFetcherService = vacancyFetcherService;
    }

    @ShellMethod(key = "find-by-id", value = "Find Vacancy by id")
    public String findById(@ShellOption(value = {"-i", "--id"},
                                        help = "Here should be an ID of vacancy",
                                        defaultValue = ShellOption.NULL
                                        ) String id) {
        VacancyEntity result = vacancyService.getVacancy(id);
        if (result == null) {
            return "Vacancy not found with ID: " + id;
        }
        return formatter.formatVacancy(result);
    }

    @ShellMethod(key = "show-vacancies", value = "Show filtered vacancies")
    public String showVacancies(
            @ShellOption(
                    value = {"-t", "--title"},
                    help = "Job title filter",
                    defaultValue = ShellOption.NULL
            ) String title,

            @ShellOption(
                    value = {"-c", "--company"},
                    help = "Company name filter",
                    defaultValue = ShellOption.NULL
            ) String company,

            @ShellOption(
                    value = {"-min", "--min-salary"},
                    help = "Minimum salary",
                    defaultValue = ShellOption.NULL
            ) Integer minSalary,

            @ShellOption(
                    value = {"-max", "--max-salary"},
                    help = "Maximum salary",
                    defaultValue = ShellOption.NULL
            ) Integer maxSalary,

            @ShellOption(
                    value = {"-p", "--page"},
                    help = "Page number",
                    defaultValue = "0"
            ) int page,

            @ShellOption(
                    value = {"-s", "--size"},
                    help = "Items per page",
                    defaultValue = "10"
            ) int size
    ) {
        Page<VacancyEntity> result = vacancyService.getVacancies(
                title, company, minSalary, maxSalary, page, size
        );

        return formatter.formatResult(result);
    }

    @ShellMethod(key = "fetch-vacancies", value = "Fetch vacancies from HeadHunter API")
    public String fetchVacancies(
            @ShellOption(
                    value = {"-t", "--text"},
                    help = "Search text",
                    defaultValue = "Developer"
            ) String searchText,

            @ShellOption(
                    value = {"-a", "--area"},
                    help = "Area code (1 - Moscow, 2 - St. Petersburg)",
                    defaultValue = "1"
            ) String area
    ) {
        int count;
        log.debug("Finding vacancies for : {}", searchText);
        count = vacancyFetcherService.fetchVacancies(searchText, area);
        return String.format("Successfully fetched %d vacancies from HeadHunter API", count);
    }

    @ShellMethod(key = "delete-vacancy", value = "Delete a vacancy from the database")
    public String deleteVacancy(
            @ShellOption(
                    value = {"-i", "--id"},
                    help = "Vacancy ID"
            ) String vacancyId
    ) {
        boolean deleted = vacancyService.deleteVacancy(vacancyId);
        if (deleted) {
            return "Vacancy with ID " + vacancyId + " was successfully deleted";
        } else {
            return "Vacancy not found with ID: " + vacancyId;
        }
    }
}