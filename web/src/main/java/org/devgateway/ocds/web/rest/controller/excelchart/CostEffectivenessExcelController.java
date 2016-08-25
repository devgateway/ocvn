package org.devgateway.ocds.web.rest.controller.excelchart;

import com.mongodb.DBObject;
import io.swagger.annotations.ApiOperation;
import org.devgateway.ocds.web.excelcharts.ChartType;
import org.devgateway.ocds.web.rest.controller.CostEffectivenessVisualsController;
import org.devgateway.ocds.web.rest.controller.GenericOCDSController;
import org.devgateway.ocds.web.rest.controller.request.GroupingFilterPagingRequest;
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
 * @since 8/19/16
 *
 * Exports an excel chart based on *Cost effectiveness* dashboard
 */
@RestController
public class CostEffectivenessExcelController extends GenericOCDSController {
    @Autowired
    private ExcelChartGenerator excelChartGenerator;

    @Autowired
    private ExcelChartHelper excelChartHelper;

    @Autowired
    private CostEffectivenessVisualsController costEffectivenessVisualsController;

    @ApiOperation(value = "Exports *Cost effectiveness* dashboard in Excel format.")
    @RequestMapping(value = "/api/ocds/costEffectivenessExcelChart", method = {RequestMethod.GET, RequestMethod.POST})
    public void costEffectivenessExcelChart(@ModelAttribute @Valid final GroupingFilterPagingRequest filter,
                                            final HttpServletResponse response) throws IOException {
        final String chartTitle = "Cost effectiveness";

        // fetch the data that will be displayed in the chart
        final List<DBObject> costEffectivenessTenderAwardAmount =
                costEffectivenessVisualsController.costEffectivenessTenderAwardAmount(filter);

        final List<?> categories = excelChartHelper.getCategoriesFromDBObject(Fields.UNDERSCORE_ID,
                costEffectivenessTenderAwardAmount);

        final List<List<? extends Number>> values = new ArrayList<>();

        final List<Number> tenderPrice = excelChartHelper.getValuesFromDBObject(costEffectivenessTenderAwardAmount,
                categories, Fields.UNDERSCORE_ID, CostEffectivenessVisualsController.Keys.TOTAL_TENDER_AMOUNT);
        final List<Number> diffPrice = excelChartHelper.getValuesFromDBObject(costEffectivenessTenderAwardAmount,
                categories,  Fields.UNDERSCORE_ID, CostEffectivenessVisualsController.Keys.DIFF_TENDER_AWARD_AMOUNT);
        values.add(tenderPrice);
        values.add(diffPrice);

        final List<String> seriesTitle = Arrays.asList(
                "Bid price",
                "Difference"
        );

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=" + chartTitle + ".xlsx");

        response.getOutputStream().write(
                excelChartGenerator.getExcelChart(
                        ChartType.stackedcol,
                        chartTitle,
                        seriesTitle,
                        categories, values));
    }
}
