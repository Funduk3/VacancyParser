package com.fedordemin.vacancyparser.components;

import com.fedordemin.vacancyparser.entities.VacancyEntity;
import com.fedordemin.vacancyparser.services.HistoryWriterService;
import com.fedordemin.vacancyparser.services.MainFacade.VacancyFacadeService;
import com.fedordemin.vacancyparser.utils.format.VacancyFormatterUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.shell.standard.*;

@ShellComponent
public class VacancyCommands {
    private final VacancyFacadeService vacancyFacadeService;
    private final VacancyFormatterUtil vacancyFormatterUtil;
    private final HistoryWriterService historyWriterService;

    @Autowired
    public VacancyCommands(
            VacancyFacadeService vacancyFacadeService, VacancyFormatterUtil vacancyFormatterUtil, HistoryWriterService historyWriterService) {
        this.vacancyFacadeService = vacancyFacadeService;
        this.vacancyFormatterUtil = vacancyFormatterUtil;
        this.historyWriterService = historyWriterService;
    }

    @ShellMethod(key = "find-by-id", value = "Find Vacancy by id")
    public String findById(@ShellOption(value = {"-i", "--id"},
                                        help = "Here should be an ID of vacancy",
                                        defaultValue = ShellOption.NULL
                                        ) String id) {
        VacancyEntity result = vacancyFacadeService.getVacancy(id);
        if (result == null) {
            return "Vacancy not found with ID: " + id;
        }
        return vacancyFormatterUtil.formatVacancy(result);
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
            ) int size,
            @ShellOption(
                    value = {"--sort"},
                    help = "Sort by field (title, salary, date)",
                    defaultValue = "default"
            ) String fieldSort
    ) {
        Page<VacancyEntity> result = vacancyFacadeService.getVacancies(
                title, company, minSalary, maxSalary, area, page, size
        );
        return vacancyFormatterUtil.formatResult(result, fieldSort);
    }

    @ShellMethod(key = "fetch-vacancies", value = "Fetch vacancies from various API")
    public void fetchVacancies(
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
    ) {
        vacancyFacadeService.fetchVacancies(searchText, company_name, area, site);
    }

    @ShellMethod(key = "delete-vacancy", value = "Delete a vacancy from the database")
    public String deleteVacancy(
            @ShellOption(
                    value = {"-i", "--id"},
                    help = "Vacancy ID"
            ) String vacancyId
    ) {
        boolean deleted = vacancyFacadeService.deleteVacancy(vacancyId);
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
        return vacancyFacadeService.export(fileType, filename);
    }

    @ShellMethod(value = "Show history of user's actions", key = "show-history")
    public String showHistory(@ShellOption(value = {"-t", "--type"},
                            help = "Type of user's action: added/deleted", defaultValue = "all")
                                  String actionType) {
        return vacancyFormatterUtil.formatHistory(historyWriterService.getAllLogs(), actionType);
    }

    @ShellMethod(value = "Will send notification if a vacancy appears", key = "set-criteria")
    public String sendNotification(
            @ShellOption(value = {"-t", "--title"}, help = "Job title", defaultValue = ShellOption.NULL) String title,
            @ShellOption(value = {"-c", "--company"}, help = "Company name", defaultValue = ShellOption.NULL) String company,
            @ShellOption(value = {"-min", "--min-salary"}, help = "Minimum salary", defaultValue = ShellOption.NULL) Integer minSalary,
            @ShellOption(value = {"-max", "--max-salary"}, help = "Maximum salary", defaultValue = ShellOption.NULL) Integer maxSalary,
            @ShellOption(value = {"-a", "--area"}, help = "Area of vacancy", defaultValue = ShellOption.NULL) String area) {

        String output = vacancyFacadeService.sendNotification(title, company, minSalary, maxSalary, area);
        System.out.println(output);
        return "Фоновый поиск вакансий запущен";
    }
}