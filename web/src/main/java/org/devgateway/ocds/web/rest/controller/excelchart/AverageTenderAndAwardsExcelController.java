package org.devgateway.ocds.web.rest.controller.excelchart;

import com.mongodb.DBObject;
import io.swagger.annotations.ApiOperation;
import org.devgateway.ocds.web.excelcharts.ChartType;
import org.devgateway.ocds.web.rest.controller.AverageTenderAndAwardPeriodsController;
import org.devgateway.ocds.web.rest.controller.GenericOCDSController;
import org.devgateway.ocds.web.rest.controller.request.YearFilterPagingRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.aggregation.Fields;
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
 * @since 8/22/16
 *
 * Exports an excel chart based on *Bid period* dashboard
 */
@RestController
public class AverageTenderAndAwardsExcelController extends GenericOCDSController {
    @Autowired
    private ExcelChartGenerator excelChartGenerator;

    @Autowired
    private ExcelChartHelper excelChartHelper;

    @Autowired
    private AverageTenderAndAwardPeriodsController averageTenderAndAwardPeriodsController;

    @ApiOperation(value = "Exports *Bid period* dashboard in Excel format.")
    @RequestMapping(value = "/api/ocds/bidPeriodExcelChart", method = {RequestMethod.GET, RequestMethod.POST})
    public void bidPeriodExcelChart(@ModelAttribute @Valid final YearFilterPagingRequest filter,
                                    final HttpServletResponse response) throws IOException {
        final String chartTitle = "Bid period";

        // fetch the data that will be displayed in the chart (we have multiple sources for this dashboard)
        final List<DBObject> averageAwardPeriod = averageTenderAndAwardPeriodsController.averageAwardPeriod(filter);
        final List<DBObject> averageTenderPeriod = averageTenderAndAwardPeriodsController.averageTenderPeriod(filter);

        final List<?> categories = excelChartHelper.getCategoriesFromDBObject(Fields.UNDERSCORE_ID,
                averageTenderPeriod, averageAwardPeriod);
        final List<List<? extends Number>> values = new ArrayList<>();

        final List<Number> valueAwards = excelChartHelper.getValuesFromDBObject(averageAwardPeriod, categories,
                Fields.UNDERSCORE_ID, AverageTenderAndAwardPeriodsController.Keys.AVERAGE_AWARD_DAYS);
        final List<Number> valueTenders = excelChartHelper.getValuesFromDBObject(averageTenderPeriod, categories,
                Fields.UNDERSCORE_ID, AverageTenderAndAwardPeriodsController.Keys.AVERAGE_TENDER_DAYS);
        values.add(valueAwards);
        values.add(valueTenders);

        final List<String> seriesTitle = Arrays.asList(
                "Award",
                "Tender");

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=" + chartTitle + ".xlsx");
        response.getOutputStream().write(
                excelChartGenerator.getExcelChart(
                        ChartType.stackedbar,
                        chartTitle,
                        seriesTitle,
                        categories, values));
    }
}
