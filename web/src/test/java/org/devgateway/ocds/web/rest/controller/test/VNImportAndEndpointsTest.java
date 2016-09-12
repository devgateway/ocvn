package org.devgateway.ocds.web.rest.controller.test;

import java.io.IOException;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.devgateway.ocds.persistence.mongo.Organization;
import org.devgateway.ocds.persistence.mongo.spring.ExcelImportService;
import org.devgateway.ocds.web.rest.controller.AverageNumberOfTenderersController;
import org.devgateway.ocds.web.rest.controller.AverageTenderAndAwardPeriodsController;
import org.devgateway.ocds.web.rest.controller.CostEffectivenessVisualsController;
import org.devgateway.ocds.web.rest.controller.request.DefaultFilterPagingRequest;
import org.devgateway.ocds.web.rest.controller.request.GroupingFilterPagingRequest;
import org.devgateway.ocds.web.rest.controller.request.OrganizationSearchRequest;
import org.devgateway.ocds.web.rest.controller.selector.ProcuringEntitySearchController;
import org.devgateway.ocvn.persistence.mongo.dao.ImportFileTypes;
import org.devgateway.toolkit.persistence.mongo.test.AbstractMongoTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.test.context.web.WebAppConfiguration;

import com.mongodb.DBObject;

/**
 * @author mihai
 *
 */
public class VNImportAndEndpointsTest extends AbstractMongoTest {

    @Autowired
    private ExcelImportService vnExcelImportService;

    @Autowired
    private CostEffectivenessVisualsController costEffectivenessVisualsController;

    @Autowired
    private AverageNumberOfTenderersController averageNumberOfTenderersController;

    @Autowired
    private AverageTenderAndAwardPeriodsController averageTenderAndAwardPeriodsController;

    @Autowired
    private ProcuringEntitySearchController procuringEntitySearchController;

    private static boolean initialized = false;

    public byte[] loadResourceStreamAsByteArray(String name) throws IOException {
        return IOUtils.toByteArray(getClass().getResourceAsStream(name));
    }

    @Before
    public void importTestData() throws IOException, InterruptedException {

        if (initialized) {
            return;
        }
        vnExcelImportService.importAllSheets(ImportFileTypes.ALL_FILE_TYPES,
                loadResourceStreamAsByteArray("/testImport/test_egp_Jun21_Import.xlsx"),
                loadResourceStreamAsByteArray("/testImport/test_Location_Table_Geocoded.xlsx"),
                loadResourceStreamAsByteArray("/testImport/test_UM_PUBINSTITU_SUPPLIERS_DQA.xlsx"), true, false);
        initialized = true;
    }

    @Test
    public void testCostEffectivenessAwardAmount() {
        List<DBObject> costEffectivenessAwardAmount = costEffectivenessVisualsController
                .costEffectivenessAwardAmount(new DefaultFilterPagingRequest());
        DBObject root = costEffectivenessAwardAmount.get(0);
        int year = (int) root.get(Fields.UNDERSCORE_ID);
        Assert.assertEquals(2014, year);

        double totalAwardAmount = (double) root.get("totalAwardAmount");
        Assert.assertEquals(2000, totalAwardAmount, 0);

    }

    @Test
    public void testCostEffectivenessTenderAmount() {
        List<DBObject> costEffectivenessTenderAmount = costEffectivenessVisualsController
                .costEffectivenessTenderAmount(new GroupingFilterPagingRequest());
        DBObject root = costEffectivenessTenderAmount.get(0);
        int year = (int) root.get(Fields.UNDERSCORE_ID);
        Assert.assertEquals(2012, year);

        double totalAwardAmount = (double) root.get("totalTenderAmount");
        Assert.assertEquals(1000, totalAwardAmount, 0);

    }

    @Test
    public void testAverageNumberOfTenderersController() {
        List<DBObject> averageNumberOfTenderers = averageNumberOfTenderersController.
                averageNumberOfTenderers(new DefaultFilterPagingRequest());

        DBObject root = averageNumberOfTenderers.get(0);
        int year = (int) root.get("year");
        Assert.assertEquals(2013, year);

        double averageNoTenderers = (double) root.get("averageNoTenderers");
        Assert.assertEquals(2, averageNoTenderers, 0);
    }

    @Test
    public void testAverageAwardPeriod() {
        List<DBObject> averageAwardPeriod = averageTenderAndAwardPeriodsController
                .averageAwardPeriod(new DefaultFilterPagingRequest());

        DBObject root = averageAwardPeriod.get(0);
        int year = (int) root.get(Fields.UNDERSCORE_ID);
        Assert.assertEquals(2014, year);

        double n = (double) root.get("averageAwardDays");
        Assert.assertEquals(536, n, 0);
    }

    @Test
    public void testAverageTenderPeriod() {
        List<DBObject> averageTenderPeriod = averageTenderAndAwardPeriodsController
                .averageTenderPeriod(new DefaultFilterPagingRequest());

        DBObject root = averageTenderPeriod.get(0);
        int year = (int) root.get(Fields.UNDERSCORE_ID);
        Assert.assertEquals(2012, year);

        double n = (double) root.get("averageTenderDays");
        Assert.assertEquals(15, n, 0);

        root = averageTenderPeriod.get(1);
        year = (int) root.get(Fields.UNDERSCORE_ID);
        Assert.assertEquals(2013, year);

        n = (double) root.get("averageTenderDays");
        Assert.assertEquals(15, n, 0);
    }

    @Test
    public void testQualityAverageTenderPeriod() {
        List<DBObject> qAverageTenderPeriod = averageTenderAndAwardPeriodsController
                .qualityAverageTenderPeriod(new DefaultFilterPagingRequest());

        DBObject root = qAverageTenderPeriod.get(0);

        int totalTenderWithStartEndDates = (int) root.get("totalTenderWithStartEndDates");
        Assert.assertEquals(2, totalTenderWithStartEndDates);

        int totalTenders = (int) root.get("totalTenders");
        Assert.assertEquals(2, totalTenders);

        double percentageTenderWithStartEndDates = (double) root.get("percentageTenderWithStartEndDates");
        Assert.assertEquals(100, percentageTenderWithStartEndDates, 0);
    }

    @Test
    public void testQualityAverageAwardPeriod() {
        List<DBObject> qAverageTenderPeriod = averageTenderAndAwardPeriodsController
                .qualityAverageAwardPeriod(new DefaultFilterPagingRequest());

        DBObject root = qAverageTenderPeriod.get(0);

        int totalAwardWithStartEndDates = (int) root.get("totalAwardWithStartEndDates");
        Assert.assertEquals(4, totalAwardWithStartEndDates);

        int totalAwards = (int) root.get("totalAwards");
        Assert.assertEquals(4, totalAwards);

        double percentageAwardWithStartEndDates = (double) root.get("percentageAwardWithStartEndDates");
        Assert.assertEquals(100, percentageAwardWithStartEndDates, 0);
    }


    @Test
    public void testProcuringEntitySearchController() {
        List<Organization> procuringEntities = procuringEntitySearchController.searchText(
                new OrganizationSearchRequest());
        Assert.assertEquals(procuringEntities.size(), 1, 0);
    }

}
