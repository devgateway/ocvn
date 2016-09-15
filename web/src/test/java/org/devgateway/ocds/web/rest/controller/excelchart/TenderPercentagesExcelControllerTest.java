package org.devgateway.ocds.web.rest.controller.excelchart;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFChart;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.charts.XSSFChartAxis;
import org.devgateway.ocds.web.rest.controller.request.YearFilterPagingRequest;
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
public class TenderPercentagesExcelControllerTest extends AbstractExcelControllerTest {
    @Autowired
    private TenderPercentagesExcelController tenderPercentagesExcelController;

    @Test
    public void cancelledFundingPercentageExcelChart() throws Exception {
        tenderPercentagesExcelController.cancelledFundingPercentageExcelChart(
                new YearFilterPagingRequest(),
                mockHttpServletResponse);

        final byte[] responseOutput = mockHttpServletResponse.getContentAsByteArray();
        final Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(responseOutput));
        Assert.assertNotNull(workbook);

        final Sheet sheet = workbook.getSheet(ChartType.area.toString());
        Assert.assertNotNull("check chart type, sheet name should be the same as the type", sheet);

        final XSSFDrawing drawing = (XSSFDrawing) sheet.getDrawingPatriarch();
        final List<XSSFChart> charts =  drawing.getCharts();
        Assert.assertEquals("number of charts", 1, charts.size());

        final XSSFChart chart = charts.get(0);
        Assert.assertEquals("chart title", "Cancelled funding (percentage)", chart.getTitle().getString());

        final List<? extends XSSFChartAxis> axis = chart.getAxis();
        Assert.assertEquals("number of axis", 2, axis.size());

        final CTChart ctChart = chart.getCTChart();
        Assert.assertEquals("Check if we have 1 area chart", 1, ctChart.getPlotArea().getAreaChartArray().length);
    }

    @Test
    public void percentTendersUsingEBidExcelChart() throws Exception {
        tenderPercentagesExcelController.percentTendersUsingEBidExcelChart(
                new YearFilterPagingRequest(),
                mockHttpServletResponse);

        final byte[] responseOutput = mockHttpServletResponse.getContentAsByteArray();
        final Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(responseOutput));
        Assert.assertNotNull(workbook);

        final Sheet sheet = workbook.getSheet(ChartType.area.toString());
        Assert.assertNotNull("check chart type, sheet name should be the same as the type", sheet);

        final XSSFDrawing drawing = (XSSFDrawing) sheet.getDrawingPatriarch();
        final List<XSSFChart> charts =  drawing.getCharts();
        Assert.assertEquals("number of charts", 1, charts.size());

        final XSSFChart chart = charts.get(0);
        Assert.assertEquals("chart title", "Percent of Tenders Using e-Bid", chart.getTitle().getString());

        final List<? extends XSSFChartAxis> axis = chart.getAxis();
        Assert.assertEquals("number of axis", 2, axis.size());

        final CTChart ctChart = chart.getCTChart();
        Assert.assertEquals("Check if we have 1 area chart", 1, ctChart.getPlotArea().getAreaChartArray().length);
    }
}
