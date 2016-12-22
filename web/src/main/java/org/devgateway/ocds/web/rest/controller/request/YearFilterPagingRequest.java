/**
 *
 */
package org.devgateway.ocds.web.rest.controller.request;

import java.util.List;

import cz.jirutka.validator.collection.constraints.EachRange;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author mpostelnicu
 *
 */
public class YearFilterPagingRequest extends DefaultFilterPagingRequest {

    @ApiModelProperty(value = "This parameter will filter the content based on year. " + "The minimum year allowed is "
            + MIN_REQ_YEAR + " and the maximum allowed is " + MAX_REQ_YEAR
            + ".It will check if the startDate and endDate are within the year range. "
            + "To check which fields are used to read start/endDate from, have a look at each endpoint definition.")
    @EachRange(min = MIN_REQ_YEAR, max = MAX_REQ_YEAR)
    protected List<Integer> year;

    @ApiModelProperty(value = "When true, this parameter will add an extra layer of monthly grouping of all results."
            + " The default is false")
    private Boolean monthly = false;

    public Boolean getMonthly() {
        return monthly;
    }

    public void setMonthly(Boolean monthly) {
        this.monthly = monthly;
    }

    /**
     *
     */
    public YearFilterPagingRequest() {
        super();
    }

    public List<Integer> getYear() {
        return year;
    }

    public void setYear(final List<Integer> year) {
        this.year = year;
    }

}
