package org.devgateway.ocds.web.rest.controller;

import com.mongodb.DBObject;
import org.devgateway.ocds.web.rest.controller.request.DefaultFilterPagingRequest;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author idobre
 * @since 9/13/16
 *
 * @see {@link AbstractEndPointControllerTest}
 */
public class TotalCancelledTendersByYearControllerTest extends AbstractEndPointControllerTest {
    @Autowired
    private TotalCancelledTendersByYearController totalCancelledTendersByYearController;

    @Test
    public void totalCancelledTendersByYear() throws Exception {
        final List<DBObject> totalCancelledTendersByYear = totalCancelledTendersByYearController
                .totalCancelledTendersByYear(new DefaultFilterPagingRequest());

        // no cancelled tenders
        Assert.assertEquals(0, totalCancelledTendersByYear.size());
    }

    @Test
    public void totalCancelledTendersByYearByRationale() throws Exception {
        final List<DBObject> totalCancelledTendersByYearByRationale = totalCancelledTendersByYearController
                .totalCancelledTendersByYearByRationale(new DefaultFilterPagingRequest());

        // no cancelled tenders
        Assert.assertEquals(0, totalCancelledTendersByYearByRationale.size());
    }
}
