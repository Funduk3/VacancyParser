package com.fedordemin.vacancyparser.utils.strategies;

import com.fedordemin.vacancyparser.entities.VacancyEntity;
import com.fedordemin.vacancyparser.utils.XlsxExportUtil;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class XlsxExportStrategy implements ExportStrategy {
    private final XlsxExportUtil xlsxUtil;

    public XlsxExportStrategy(XlsxExportUtil xlsxUtil) {
        this.xlsxUtil = xlsxUtil;
    }

    @Override
    public void export(List<VacancyEntity> data, String filename) throws IOException {
        xlsxUtil.export(data, filename);
    }
}
