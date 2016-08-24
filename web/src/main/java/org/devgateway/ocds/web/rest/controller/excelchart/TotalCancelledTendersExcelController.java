package org.devgateway.ocds.web.rest.controller.excelchart;

import com.mongodb.DBObject;
import io.swagger.annotations.ApiOperation;
import org.devgateway.ocds.web.excelcharts.ChartType;
import org.devgateway.ocds.web.rest.controller.GenericOCDSController;
import org.devgateway.ocds.web.rest.controller.TotalCancelledTendersByYearController;
import org.devgateway.ocds.web.rest.controller.request.DefaultFilterPagingRequest;
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
 * @since 8/23/16
 *
 * Exports an excel chart based on *Cancelled funding* dashboard
 */
@RestController
public class TotalCancelledTendersExcelController extends GenericOCDSController {
    @Autowired
    private ExcelChartGenerator excelChartGenerator;

    @Autowired
    private ExcelChartHelper excelChartHelper;

    @Autowired
    private TotalCancelledTendersByYearController totalCancelledTendersByYearController;

    @ApiOperation(value = "Exports *Cancelled funding* dashboard in Excel format.")
    @RequestMapping(value = "/api/ocds/cancelledFundingExcelChart", method = {RequestMethod.GET, RequestMethod.POST})
    public void cancelledFundingExcelChart(@ModelAttribute @Valid final DefaultFilterPagingRequest filter,
                                           final HttpServletResponse response) throws IOException {
        final String chartTitle = "Cancelled funding";

        // fetch the data that will be displayed in the chart
        final List<DBObject> totalCancelledTenders = totalCancelledTendersByYearController
                .totalCancelledTendersByYear(filter);

        final List<?> categories = excelChartHelper.getCategoriesFromDBObject(Fields.UNDERSCORE_ID,
                totalCancelledTenders);
        final List<List<? extends Number>> values = new ArrayList<>();

        final List<Number> cancelledAmount = excelChartHelper.getValuesFromDBObject(totalCancelledTenders, categories,
                Fields.UNDERSCORE_ID, TotalCancelledTendersByYearController.Keys.TOTAL_CANCELLED_TENDERS_AMOUNT);
        values.add(cancelledAmount);

        final List<String> seriesTitle = Arrays.asList(
                "Amount");

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
