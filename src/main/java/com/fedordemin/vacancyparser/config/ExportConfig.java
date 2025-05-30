package com.fedordemin.vacancyparser.config;

import com.fedordemin.vacancyparser.utils.export.CsvExportUtil;
import com.fedordemin.vacancyparser.utils.export.ExportUtil;
import com.fedordemin.vacancyparser.utils.export.JsonExportUtil;
import com.fedordemin.vacancyparser.utils.export.XlsxExportUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class ExportConfig {

    @Bean
    public Map<String, ExportUtil> exportStrategies(CsvExportUtil csvExportUtil,
                                                    XlsxExportUtil xlsxExportUtil,
                                                    JsonExportUtil jsonExportUtil) {
        Map<String, ExportUtil> strategies = new HashMap<>();
        strategies.put("csv", csvExportUtil);
        strategies.put("xlsx", xlsxExportUtil);
        strategies.put("json", jsonExportUtil);
        return strategies;
    }
}