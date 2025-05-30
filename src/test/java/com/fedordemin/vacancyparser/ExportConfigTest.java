package com.fedordemin.vacancyparser;

import com.fedordemin.vacancyparser.config.ExportConfig;
import com.fedordemin.vacancyparser.utils.export.CsvExportUtil;
import com.fedordemin.vacancyparser.utils.export.ExportUtil;
import com.fedordemin.vacancyparser.utils.export.JsonExportUtil;
import com.fedordemin.vacancyparser.utils.export.XlsxExportUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ExportConfigTest {

    @Mock
    private CsvExportUtil csvExportUtil;

    @Mock
    private XlsxExportUtil xlsxExportUtil;

    @Mock
    private JsonExportUtil jsonExportUtil;

    @Test
    void testExportStrategies() {
        ExportConfig config = new ExportConfig();

        Map<String, ExportUtil> strategies = config.exportStrategies(
                csvExportUtil, xlsxExportUtil, jsonExportUtil);

        assertNotNull(strategies);
        assertEquals(3, strategies.size());
        assertTrue(strategies.containsKey("csv"));
        assertTrue(strategies.containsKey("xlsx"));
        assertTrue(strategies.containsKey("json"));
        assertSame(csvExportUtil, strategies.get("csv"));
        assertSame(xlsxExportUtil, strategies.get("xlsx"));
        assertSame(jsonExportUtil, strategies.get("json"));
    }
}