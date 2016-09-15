package org.devgateway.ocds.web.rest.controller;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.devgateway.ocds.web.rest.controller.request.DefaultFilterPagingRequest;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author idobre
 * @since 9/13/16
 *
 * @see {@link AbstractEndPointControllerTest}
 */
public class FundingByLocationControllerTest extends AbstractEndPointControllerTest {
    @Autowired
    private FundingByLocationController fundingByLocationController;

    @Test
    public void fundingByTenderDeliveryLocation() throws Exception {
        final List<DBObject> fundingByTenderDeliveryLocation = fundingByLocationController
                .fundingByTenderDeliveryLocation(new DefaultFilterPagingRequest());

        final DBObject first = fundingByTenderDeliveryLocation.get(0);
        int year = (int) first.get(FundingByLocationController.Keys.YEAR);
        BasicDBObject deliveryLocation = (BasicDBObject) first
                .get(FundingByLocationController.Keys.ITEMS_DELIVERY_LOCATION);
        BasicDBObject geometry = (BasicDBObject) deliveryLocation.get("geometry");
        String geometryType = (String) geometry.get("type");
        List<Double> coordinates = (List<Double>) geometry.get("coordinates");
        double totalTendersAmount = (double) first.get(FundingByLocationController.Keys.TOTAL_TENDERS_AMOUNT);
        int tendersCount = (int) first.get(FundingByLocationController.Keys.TENDERS_COUNT);

        Assert.assertEquals(2015, year);
        Assert.assertEquals("Point", geometryType);
        Assert.assertEquals(new ArrayList<>(Arrays.asList(45.9432, 24.9668)), coordinates);
        Assert.assertEquals(9000.0, totalTendersAmount, 0);
        Assert.assertEquals(1, tendersCount);
    }

    @Test
    public void qualityFundingByTenderDeliveryLocation() throws Exception {
        final List<DBObject> qualityFundingByTenderDeliveryLocation = fundingByLocationController
                .qualityFundingByTenderDeliveryLocation(new DefaultFilterPagingRequest());

        final DBObject first = qualityFundingByTenderDeliveryLocation.get(0);
        int totalTendersWithStartDate = (int) first.get(FundingByLocationController.Keys.TOTAL_TENDERS_WITH_START_DATE);
        int totalTendersWithStartDateAndLocation = (int) first
                .get(FundingByLocationController.Keys.TOTAL_TENDERS_WITH_START_DATE_AND_LOCATION);
        double percentTendersWithStartDateAndLocation = (double) first
                .get(FundingByLocationController.Keys.PERCENT_TENDERS_WITH_START_DATE_AND_LOCATION);

        Assert.assertEquals(3, totalTendersWithStartDate);
        Assert.assertEquals(1, totalTendersWithStartDateAndLocation);
        Assert.assertEquals(33.33, percentTendersWithStartDateAndLocation, 1E-2);
    }
}
