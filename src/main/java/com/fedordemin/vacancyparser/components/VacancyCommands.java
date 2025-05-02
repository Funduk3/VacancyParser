package com.fedordemin.vacancyparser.components;

import com.fedordemin.vacancyparser.models.entities.VacancyEntity;
import com.fedordemin.vacancyparser.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.shell.standard.*;

@ShellComponent
public class VacancyCommands {
    private final HHApiService hhApiService;
    private final VacancyService vacancyService;
    private final FormatterService formatter;

    @Autowired
    public VacancyCommands(HHApiService hhApiService, VacancyService vacancyService, FormatterService formatter) {
        this.hhApiService = hhApiService;
        this.vacancyService = vacancyService;
        this.formatter = formatter;
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

    @ShellMethod(key = "list-saved", value = "List saved vacancies")
    public String listSavedVacancies() {
        return vacancyService.printListVacancies();
    }
}