package org.devgateway.ocds.web.rest.controller;

import com.mongodb.DBObject;
import org.devgateway.ocds.web.rest.controller.request.DefaultFilterPagingRequest;
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
public class CountPlansTendersAwardsControllerTest extends AbstractEndPointControllerTest {
    @Autowired
    private CountPlansTendersAwardsController countPlansTendersAwardsController;

    @Test
    public void countTendersByYear() throws Exception {
        final List<DBObject> countTendersByYear = countPlansTendersAwardsController
                .countTendersByYear(new DefaultFilterPagingRequest());

        final DBObject first = countTendersByYear.get(0);
        int year = (int) first.get(Fields.UNDERSCORE_ID);
        int count = (int) first.get(CountPlansTendersAwardsController.Keys.COUNT);
        Assert.assertEquals(2014, year);
        Assert.assertEquals(1, count, 0);

        final DBObject second = countTendersByYear.get(1);
        year = (int) second.get(Fields.UNDERSCORE_ID);
        count = (int) second.get(CountPlansTendersAwardsController.Keys.COUNT);
        Assert.assertEquals(2015, year);
        Assert.assertEquals(2, count, 0);
    }

    @Test
    public void countAwardsByYear() throws Exception {
        final List<DBObject> countAwardsByYear = countPlansTendersAwardsController
                .countAwardsByYear(new DefaultFilterPagingRequest());

        final DBObject first = countAwardsByYear.get(0);
        int year = (int) first.get(Fields.UNDERSCORE_ID);
        int count = (int) first.get(CountPlansTendersAwardsController.Keys.COUNT);
        Assert.assertEquals(2015, year);
        Assert.assertEquals(1, count, 0);

        final DBObject second = countAwardsByYear.get(1);
        year = (int) second.get(Fields.UNDERSCORE_ID);
        count = (int) second.get(CountPlansTendersAwardsController.Keys.COUNT);
        Assert.assertEquals(2016, year);
        Assert.assertEquals(2, count, 0);
    }

}
