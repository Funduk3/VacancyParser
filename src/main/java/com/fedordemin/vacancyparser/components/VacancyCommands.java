package com.fedordemin.vacancyparser.components;

import com.fedordemin.vacancyparser.entities.VacancyEntity;
import com.fedordemin.vacancyparser.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.shell.standard.*;

@ShellComponent
public class VacancyCommands {
    private final VacancyService vacancyService;

    @Autowired
    public VacancyCommands(
            VacancyService vacancyService) {
        this.vacancyService = vacancyService;
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
        return vacancyService.formatVacancy(result);
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
                    value = {"-a", "--area"},
                    help = "Area of vacancy",
                    defaultValue = ShellOption.NULL
            ) String area,
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
                title, company, minSalary, maxSalary, area, page, size
        );

        return vacancyService.formatResult(result);
    }

    @ShellMethod(key = "fetch-vacancies", value = "Fetch vacancies from various API")
    public String fetchVacancies(
            @ShellOption(
                    value = {"-t", "--title"},
                    help = "Search text",
                    defaultValue = ShellOption.NULL
            ) String searchText,
            @ShellOption(
                    value = {"-c", "--company"},
                    help = "Company name",
                    defaultValue = ShellOption.NULL
            ) String company_name,
            @ShellOption(
                    value = {"-a", "--area"},
                    help = "Area code (1 - Moscow, 2 - St. Petersburg)",
                    defaultValue = ShellOption.NULL
            ) String area,
            @ShellOption(
                    value = {"-s", "--website"},
                    help = "Website to find data from (hh.ru, trudvsem.ru)",
                    defaultValue = "hh.ru"
            ) String site
    ) throws Exception {
        vacancyService.fetchVacancies(searchText, company_name, area, site);
        return "Successfully fetched vacancies";
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

    @ShellMethod(value = "Export vacancies to different types", key = "export-vacancies")
    public String exportEmployees(@ShellOption(value = {"-t", "--type"},
                                          help = "Type of the export file",
                                  defaultValue = "csv") String fileType,
                                @ShellOption(defaultValue = "vacancies") String filename) {
        return vacancyService.export(fileType, filename);
    }

    @ShellMethod(value = "Show history of user's actions", key = "show-history")
    public String showHistory(@ShellOption(value = {"-t", "--type"},
    help = "Type of user's action: added/deleted", defaultValue = "all") String actionType) {
        return vacancyService.formatHistory(actionType);
    }

    @ShellMethod(value = "Will send notification if a vacancy appears", key = "set-criteria")
    public String sendNotification(
            @ShellOption(value = {"-t", "--title"}, help = "Job title", defaultValue = ShellOption.NULL) String title,
            @ShellOption(value = {"-c", "--company"}, help = "Company name", defaultValue = ShellOption.NULL) String company,
            @ShellOption(value = {"-min", "--min-salary"}, help = "Minimum salary", defaultValue = ShellOption.NULL) Integer minSalary,
            @ShellOption(value = {"-max", "--max-salary"}, help = "Maximum salary", defaultValue = ShellOption.NULL) Integer maxSalary,
            @ShellOption(value = {"-a", "--area"}, help = "Area of vacancy", defaultValue = ShellOption.NULL) String area) {

        String output = vacancyService.sendNotification(title, company, minSalary, maxSalary, area);
        System.out.println(output);
        return "Фоновый поиск вакансий запущен";
    }
}