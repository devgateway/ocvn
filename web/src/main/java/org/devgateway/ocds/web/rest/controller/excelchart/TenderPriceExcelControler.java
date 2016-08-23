package org.devgateway.ocds.web.rest.controller.excelchart;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import io.swagger.annotations.ApiOperation;
import org.devgateway.ocds.web.excelcharts.ChartType;
import org.devgateway.ocds.web.rest.controller.GenericOCDSController;
import org.devgateway.ocds.web.rest.controller.TenderPriceByTypeYearController;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author idobre
 * @since 8/22/16
 *
 * Exports an excel chart based on *Bid selection method* dashboard
 */
@RestController
public class TenderPriceExcelControler extends GenericOCDSController {
    @Autowired
    private ExcelChartGenerator excelChartGenerator;

    @Autowired
    private ExcelChartHelper excelChartHelper;

    @Autowired
    private TenderPriceByTypeYearController tenderPriceByTypeYearController;

    @ApiOperation(value = "Exports *Bid selection* dashboard in Excel format.")
    @RequestMapping(value = "/api/ocds/bidSelectionExcelChart", method = {RequestMethod.GET, RequestMethod.POST})
    public void bidSelectionExcelChart(@ModelAttribute @Valid final DefaultFilterPagingRequest filter,
                                       final HttpServletResponse response) throws IOException {
        final String chartTitle = "Bid selection";

        // fetch the data that will be displayed in the chart
        final List<DBObject> tenderPriceByBidSelection =
                tenderPriceByTypeYearController.tenderPriceByBidSelectionMethodYear(filter);


        // TODO - change this when we update the endpoint
        final LinkedHashMap<String, DBObject> result = new LinkedHashMap<>();
        // sum the amounts
        tenderPriceByBidSelection.forEach(dbobj -> {
                    if (result.containsKey(
                            dbobj.get(TenderPriceByTypeYearController.Keys.PROCUREMENT_METHOD_DETAILS))) {
                        Map<?, ?> map = dbobj.toMap();
                        Map<String, Double> mapResponse =
                                result.get(dbobj.get(TenderPriceByTypeYearController.Keys.PROCUREMENT_METHOD_DETAILS))
                                        .toMap();
                        Object tenderValue1 = map.get(TenderPriceByTypeYearController.Keys.TOTAL_TENDER_AMOUNT);
                        Object tenderValue2 = mapResponse.get(TenderPriceByTypeYearController.Keys.TOTAL_TENDER_AMOUNT);
                        mapResponse.put(TenderPriceByTypeYearController.Keys.TOTAL_TENDER_AMOUNT,
                                (double) tenderValue1 + (double) tenderValue2);
                        DBObject dbObject = new BasicDBObject(mapResponse);
                        result.put(TenderPriceByTypeYearController.Keys.TOTAL_TENDER_AMOUNT, dbObject);
                    } else {
                        result.put((String) dbobj.get(
                                TenderPriceByTypeYearController.Keys.PROCUREMENT_METHOD_DETAILS
                        ), dbobj);
                    }
                }
        );
        List<DBObject> respCollection = new ArrayList(result.values());

        final List<?> categories = excelChartHelper.getCategoriesFromDBObject(
                TenderPriceByTypeYearController.Keys.PROCUREMENT_METHOD_DETAILS, respCollection);

        final List<List<? extends Number>> values = new ArrayList<>();

        final List<Number> totalTenderAmount = excelChartHelper.getValuesFromDBObject(respCollection,
                categories, TenderPriceByTypeYearController.Keys.PROCUREMENT_METHOD_DETAILS,
                TenderPriceByTypeYearController.Keys.TOTAL_TENDER_AMOUNT);
        values.add(totalTenderAmount);

        final List<String> seriesTitle = Arrays.asList(
                "Bid selection method");

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
