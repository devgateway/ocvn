package org.devgateway.ocds.web.rest.controller;

import com.mongodb.DBObject;
import org.devgateway.ocds.web.rest.controller.request.DefaultFilterPagingRequest;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author idobre
 * @since 9/12/16
 *
 * @see {@link AbstractEndPointControllerTest}
 */
public class AverageNumberOfTenderersControllerTest extends AbstractEndPointControllerTest {
    @Autowired
    private AverageNumberOfTenderersController averageNumberOfTenderersController;

    @Test
    public void averageNumberOfTenderers() throws Exception {
        final List<DBObject> averageNumberOfTenderers = averageNumberOfTenderersController
                .averageNumberOfTenderers(new DefaultFilterPagingRequest());

        final DBObject first = averageNumberOfTenderers.get(0);
        int year = (int) first.get(AverageNumberOfTenderersController.Keys.YEAR);
        double averageNoTenderers = (double) first.get(AverageNumberOfTenderersController.Keys.AVERAGE_NO_OF_TENDERERS);
        Assert.assertEquals(2015, year);
        Assert.assertEquals(7.0, averageNoTenderers, 0);

        final DBObject second = averageNumberOfTenderers.get(1);
        year = (int) second.get(AverageNumberOfTenderersController.Keys.YEAR);
        averageNoTenderers = (double) second.get(AverageNumberOfTenderersController.Keys.AVERAGE_NO_OF_TENDERERS);
        Assert.assertEquals(2014, year);
        Assert.assertEquals(5.0, averageNoTenderers, 0);
    }
}
