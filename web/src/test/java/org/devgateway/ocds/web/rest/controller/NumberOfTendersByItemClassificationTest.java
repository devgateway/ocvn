package org.devgateway.ocds.web.rest.controller;

import com.mongodb.DBObject;
import org.devgateway.ocds.web.rest.controller.request.YearFilterPagingRequest;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.aggregation.Fields;

import java.util.List;

/**
 * @author idobre
 * @since 9/13/16
 *
 * @see {@link AbstractEndPointControllerTest}
 */
public class NumberOfTendersByItemClassificationTest extends AbstractEndPointControllerTest {
    @Autowired
    private NumberOfTendersByItemClassification numberOfTendersByItemClassification;

    @Test
    public void numberOfTendersByItemClassification() throws Exception {
        final List<DBObject> numberOfTendersByItem = numberOfTendersByItemClassification
                .numberOfTendersByItemClassification(new YearFilterPagingRequest());

        logger.error(numberOfTendersByItem);

        final DBObject first = numberOfTendersByItem.get(0);
        String id = (String) first.get(Fields.UNDERSCORE_ID);
        String description = (String) first.get(NumberOfTendersByItemClassification.Keys.DESCRIPTION);
        int totalTenders = (int) first.get(NumberOfTendersByItemClassification.Keys.TOTAL_TENDERS);
        Assert.assertEquals("09100000", id);
        Assert.assertEquals("Fuels", description);
        Assert.assertEquals(1, totalTenders);

        final DBObject second = numberOfTendersByItem.get(1);
        id = (String) second.get(Fields.UNDERSCORE_ID);
        description = (String) second.get(NumberOfTendersByItemClassification.Keys.DESCRIPTION);
        totalTenders = (int) second.get(NumberOfTendersByItemClassification.Keys.TOTAL_TENDERS);
        Assert.assertEquals("45233130", id);
        Assert.assertEquals("Construction work for highways", description);
        Assert.assertEquals(2, totalTenders);
    }
}
