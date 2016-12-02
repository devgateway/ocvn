package org.devgateway.ocds.web.rest.controller.flags;

import org.devgateway.ocds.web.rest.controller.GenericOCDSController;

/**
 * Created by mpostelnicu on 12/2/2016.
 */
public abstract class AbstractFlagController extends GenericOCDSController {

    protected abstract String getFlagProperty();


    protected String getYearProperty() {
        return "tender.tenderPeriod.startDate";
    }

}
