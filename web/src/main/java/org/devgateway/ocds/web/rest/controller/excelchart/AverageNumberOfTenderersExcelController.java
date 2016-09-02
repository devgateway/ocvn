package org.devgateway.ocds.web.rest.controller.excelchart;

import com.mongodb.DBObject;
import io.swagger.annotations.ApiOperation;
import org.devgateway.ocds.web.excelcharts.ChartType;
import org.devgateway.ocds.web.rest.controller.AverageNumberOfTenderersController;
import org.devgateway.ocds.web.rest.controller.GenericOCDSController;
import org.devgateway.ocds.web.rest.controller.request.YearFilterPagingRequest;
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
 * @since 8/22/16
 *
 * Exports an excel chart based on *Average number of bids* dashboard
 */
@RestController
public class AverageNumberOfTenderersExcelController extends GenericOCDSController {
    @Autowired
    private ExcelChartGenerator excelChartGenerator;

    @Autowired
    private ExcelChartHelper excelChartHelper;

    @Autowired
    private AverageNumberOfTenderersController averageNumberOfTenderersController;

    @ApiOperation(value = "Exports *Average number of bids* dashboard in Excel format.")
    @RequestMapping(value = "/api/ocds/averageNumberBidsExcelChart", method = {RequestMethod.GET, RequestMethod.POST})
    public void averageNumberBidsExcelChart(@ModelAttribute @Valid final YearFilterPagingRequest filter,
                                            final HttpServletResponse response) throws IOException {
        final String chartTitle = "Average number of bids";

        // fetch the data that will be displayed in the chart
        final List<DBObject> averageNumberOfTenderers =
                averageNumberOfTenderersController.averageNumberOfTenderers(filter);

        final List<?> categories = excelChartHelper.getCategoriesFromDBObject(
                AverageNumberOfTenderersController.Keys.YEAR, averageNumberOfTenderers);

        final List<List<? extends Number>> values = new ArrayList<>();

        final List<Number> totalTenderAmount = excelChartHelper.getValuesFromDBObject(averageNumberOfTenderers,
                categories, AverageNumberOfTenderersController.Keys.YEAR,
                AverageNumberOfTenderersController.Keys.AVERAGE_NO_OF_TENDERERS);
        values.add(totalTenderAmount);

        final List<String> seriesTitle = Arrays.asList(
                "Average Number");

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=" + chartTitle + ".xlsx");
        response.getOutputStream().write(
                excelChartGenerator.getExcelChart(
                        ChartType.barcol,
                        chartTitle,
                        seriesTitle,
                        categories, values));

    }

}
