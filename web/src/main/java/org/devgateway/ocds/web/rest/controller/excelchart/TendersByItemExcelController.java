package org.devgateway.ocds.web.rest.controller.excelchart;

import com.mongodb.DBObject;
import io.swagger.annotations.ApiOperation;
import org.devgateway.ocds.web.rest.controller.GenericOCDSController;
import org.devgateway.ocds.web.rest.controller.TendersByItemClassification;
import org.devgateway.ocds.web.rest.controller.request.YearFilterPagingRequest;
import org.devgateway.toolkit.web.excelcharts.ChartType;
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
 * @since 9/4/16
 *
 * Exports an excel chart based on *Number of bids by item* dashboard
 */
@RestController
public class TendersByItemExcelController extends GenericOCDSController {
    @Autowired
    private ExcelChartGenerator excelChartGenerator;

    @Autowired
    private ExcelChartHelper excelChartHelper;

    @Autowired
    private TendersByItemClassification tendersByItemClassification;

    @ApiOperation(value = "Exports *Number of bids by item* dashboard in Excel format.")
    @RequestMapping(value = "/api/ocds/tendersByItemExcelChart",
            method = {RequestMethod.GET, RequestMethod.POST})
    public void numberOfTendersByItemExcelChart(@ModelAttribute @Valid final YearFilterPagingRequest filter,
                                                final HttpServletResponse response) throws IOException {
        final String chartTitle = "Number of bids by item";

        // fetch the data that will be displayed in the chart
        final List<DBObject> numberOfTendersByItem =
                tendersByItemClassification.tendersByItemClassification(filter);

        final List<?> categories = excelChartHelper.getCategoriesFromDBObject(
                TendersByItemClassification.Keys.DESCRIPTION, numberOfTendersByItem);

        final List<List<? extends Number>> values = new ArrayList<>();

        final List<Number> totalTenderAmount = excelChartHelper.getValuesFromDBObject(numberOfTendersByItem,
                categories, TendersByItemClassification.Keys.DESCRIPTION,
                TendersByItemClassification.Keys.TOTAL_TENDERS);
        if (!totalTenderAmount.isEmpty()) {
            values.add(totalTenderAmount);
        }

        // check if we have anything to display before setting the *seriesTitle*.
        final List<String> seriesTitle;
        if (!values.isEmpty()) {
            seriesTitle = Arrays.asList(
                    "Item");
        } else {
            seriesTitle = new ArrayList<>();
        }

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
