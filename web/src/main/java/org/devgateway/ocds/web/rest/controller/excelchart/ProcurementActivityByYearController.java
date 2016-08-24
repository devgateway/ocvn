package org.devgateway.ocds.web.rest.controller.excelchart;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.devgateway.ocds.web.excelcharts.ChartType;
import org.devgateway.ocds.web.rest.controller.CountPlansTendersAwardsController;
import org.devgateway.ocds.web.rest.controller.GenericOCDSController;
import org.devgateway.ocds.web.rest.controller.request.YearFilterPagingRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.mongodb.DBObject;

import io.swagger.annotations.ApiOperation;

/**
 * @author idobre
 * @since 8/17/16
 *
 * Exports an excel chart based on *Procurement activity by year* dashboard
 */
@RestController
public class ProcurementActivityByYearController extends GenericOCDSController {
    @Autowired
    private ExcelChartGenerator excelChartGenerator;

    @Autowired
    private ExcelChartHelper excelChartHelper;

    @Autowired
    private CountPlansTendersAwardsController countPlansTendersAwardsController;

    @ApiOperation(value = "Exports *Procurement activity by year* dashboard in Excel format.")
    @RequestMapping(value = "/api/ocds/procurementActivityExcelChart", method = {RequestMethod.GET, RequestMethod.POST})
    public void procurementActivityExcelChart(@ModelAttribute @Valid final YearFilterPagingRequest filter,
                                              final HttpServletResponse response) throws IOException {
        final String chartTitle = "procurement activity by year";

        // fetch the data that will be displayed in the chart (we have multiple sources for this dashboard)
        final List<DBObject> countAwardsByYear = countPlansTendersAwardsController.countAwardsByYear(filter);
        final List<DBObject> countTendersByYear = countPlansTendersAwardsController.countTendersByYear(filter);

        final List<?> categories = excelChartHelper.getCategoriesFromDBObject(Fields.UNDERSCORE_ID,
                countAwardsByYear, countTendersByYear);
        final List<List<? extends Number>> values = new ArrayList<>();

        final List<Number> valueAwards = excelChartHelper.getValuesFromDBObject(countAwardsByYear, categories,
                Fields.UNDERSCORE_ID, CountPlansTendersAwardsController.Keys.COUNT);
        final List<Number> valueTenders = excelChartHelper.getValuesFromDBObject(countTendersByYear, categories,
                Fields.UNDERSCORE_ID, CountPlansTendersAwardsController.Keys.COUNT);
        values.add(valueAwards);
        values.add(valueTenders);

        final List<String> seriesTitle = Arrays.asList(
                "Award",
                "Tender");

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=" + chartTitle + ".xlsx");
        response.getOutputStream().write(
                excelChartGenerator.getExcelChart(
                        ChartType.line,
                        chartTitle,
                        seriesTitle,
                        categories, values));
    }
}
