package org.devgateway.toolkit.web.excelcharts;

import junit.framework.Assert;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFChart;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.charts.XSSFCategoryAxis;
import org.apache.poi.xssf.usermodel.charts.XSSFChartAxis;
import org.apache.poi.xssf.usermodel.charts.XSSFValueAxis;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

/**
 * @author idobre
 * @since 9/8/16
 */
public class ExcelChartDefaultTest {
    private static final List<?> CATEGORIES = Arrays.asList(
            "cat 1",
            "cat 2",
            "cat 3",
            "cat 4",
            "cat 5"
    );

    private static final List<List<? extends Number>> VALUES = Arrays.asList(
            Arrays.asList(5, 7, 10, 12, 6),
            Arrays.asList(20, 12, 10, 5, 14)
    );

    @Test
    public void createWorkbook() throws Exception {
        final ExcelChart excelChart = new ExcelChartDefault("line chart", ChartType.line, CATEGORIES, VALUES);
        excelChart.configureSeriesTitle(
                Arrays.asList(
                        "foo",
                        "bar"
                ));
        final Workbook workbook = excelChart.createWorkbook();
        Assert.assertNotNull(workbook);

        final Sheet sheet = workbook.getSheet(ChartType.line.toString());
        Assert.assertNotNull(sheet);

        final XSSFDrawing drawing = (XSSFDrawing) sheet.getDrawingPatriarch();
        final List<XSSFChart> charts =  drawing.getCharts();
        Assert.assertEquals("number of charts", 1, charts.size());

        final XSSFChart chart = charts.get(0);
        Assert.assertEquals("chart title", "line chart", chart.getTitle().getString());

        final List<? extends XSSFChartAxis> axis = chart.getAxis();
        Assert.assertEquals("number of axis", 2, axis.size());
        Assert.assertTrue("category axis", axis.get(0) instanceof XSSFCategoryAxis);
        Assert.assertTrue("category axis", axis.get(1) instanceof XSSFValueAxis);
    }
}
