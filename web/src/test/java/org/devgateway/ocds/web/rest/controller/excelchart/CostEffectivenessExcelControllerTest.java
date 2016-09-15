package org.devgateway.ocds.web.rest.controller.excelchart;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFChart;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.charts.XSSFChartAxis;
import org.devgateway.ocds.web.rest.controller.request.GroupingFilterPagingRequest;
import org.devgateway.toolkit.web.excelcharts.ChartType;
import org.junit.Assert;
import org.junit.Test;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTChart;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayInputStream;
import java.util.List;

/**
 * @author idobre
 * @since 9/14/16
 *
 * @see {@link AbstractExcelControllerTest}
 */
public class CostEffectivenessExcelControllerTest extends AbstractExcelControllerTest {
    @Autowired
    private CostEffectivenessExcelController costEffectivenessExcelController;

    @Test
    public void costEffectivenessExcelChart() throws Exception {
        costEffectivenessExcelController.costEffectivenessExcelChart(
                new GroupingFilterPagingRequest(),
                mockHttpServletResponse);

        final byte[] responseOutput = mockHttpServletResponse.getContentAsByteArray();
        final Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(responseOutput));
        Assert.assertNotNull(workbook);

        final Sheet sheet = workbook.getSheet(ChartType.stackedcol.toString());
        Assert.assertNotNull("check chart type, sheet name should be the same as the type", sheet);

        final XSSFDrawing drawing = (XSSFDrawing) sheet.getDrawingPatriarch();
        final List<XSSFChart> charts =  drawing.getCharts();
        Assert.assertEquals("number of charts", 1, charts.size());

        final XSSFChart chart = charts.get(0);
        Assert.assertEquals("chart title", "Cost effectiveness", chart.getTitle().getString());

        final List<? extends XSSFChartAxis> axis = chart.getAxis();
        Assert.assertEquals("number of axis", 2, axis.size());

        final CTChart ctChart = chart.getCTChart();
        Assert.assertEquals("Check if we have 1 bar chart", 1, ctChart.getPlotArea().getBarChartArray().length);
    }

}
