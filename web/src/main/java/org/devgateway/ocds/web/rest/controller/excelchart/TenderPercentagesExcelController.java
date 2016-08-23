package org.devgateway.ocds.web.rest.controller.excelchart;

import com.mongodb.DBObject;
import io.swagger.annotations.ApiOperation;
import org.devgateway.ocds.web.excelcharts.ChartType;
import org.devgateway.ocds.web.rest.controller.GenericOCDSController;
import org.devgateway.ocds.web.rest.controller.TenderPercentagesController;
import org.devgateway.ocds.web.rest.controller.request.DefaultFilterPagingRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author idobre
 * @since 8/23/16
 *
 * Exports an excel chart based on *Cancelled funding (percentage)* dashboard
 */
@RestController
public class TenderPercentagesExcelController extends GenericOCDSController {
    @Autowired
    private ExcelChartGenerator excelChartGenerator;

    @Autowired
    private ExcelChartHelper excelChartHelper;

    @Autowired
    private TenderPercentagesController tenderPercentagesController;

    @ApiOperation(value = "Exports *Cancelled funding (percentage)* dashboard in Excel format.")
    @RequestMapping(value = "/api/ocds/cancelledFundingPercentageExcelChart",
            method = {RequestMethod.GET, RequestMethod.POST})
    public void cancelledFundingPercentageExcelChart(@ModelAttribute @Valid final DefaultFilterPagingRequest filter,
                            final HttpServletResponse response) throws IOException {
        final String chartTitle = "Cancelled funding (percentage)";

        // fetch the data that will be displayed in the chart
        final List<DBObject> totalCancelledTenders = tenderPercentagesController.percentTendersCancelled(filter);

        final List<?> categories = excelChartHelper.getCategoriesFromDBObject(TenderPercentagesController.Keys.YEAR,
                totalCancelledTenders);
        final List<List<? extends Number>> values = new ArrayList<>();

        final List<Number> percentCancelled = excelChartHelper.getValuesFromDBObject(totalCancelledTenders, categories,
                TenderPercentagesController.Keys.YEAR, TenderPercentagesController.Keys.PERCENT_CANCELLED);
        values.add(percentCancelled);

        final List<String> seriesTitle = Arrays.asList(
                "Percent");

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=" + chartTitle + ".xlsx");
        response.getOutputStream().write(
                excelChartGenerator.getExcelChart(
                        ChartType.area,
                        chartTitle,
                        seriesTitle,
                        categories, values));
    }

    @ApiOperation(value = "Exports *Percent of Tenders Using e-Bid* dashboard in Excel format.")
    @RequestMapping(value = "/api/ocds/percentTendersUsingEBidExcelChart",
            method = {RequestMethod.GET, RequestMethod.POST})
    public void percentTendersUsingEBidExcelChart(@ModelAttribute @Valid final DefaultFilterPagingRequest filter,
                            final HttpServletResponse response) throws IOException {
        final String chartTitle = "Percent of Tenders Using e-Bid";

        // fetch the data that will be displayed in the chart
        final List<DBObject> totalCancelledTenders = tenderPercentagesController.percentTendersUsingEBid(filter);

        final List<?> categories = excelChartHelper.getCategoriesFromDBObject(TenderPercentagesController.Keys.YEAR,
                totalCancelledTenders);
        final List<List<? extends Number>> values = new ArrayList<>();

        final List<Number> percentUsingEBid = excelChartHelper.getValuesFromDBObject(totalCancelledTenders, categories,
                TenderPercentagesController.Keys.YEAR, TenderPercentagesController.Keys.PERCENTAGE_TENDERS_USING_EBID);
        values.add(percentUsingEBid);

        final List<String> seriesTitle = Arrays.asList(
                "Percent");

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=" + chartTitle + ".xlsx");
        response.getOutputStream().write(
                excelChartGenerator.getExcelChart(
                        ChartType.area,
                        chartTitle,
                        seriesTitle,
                        categories, values));
    }
}
