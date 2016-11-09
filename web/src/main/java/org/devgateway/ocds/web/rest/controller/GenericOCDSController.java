/**
 *
 */
package org.devgateway.ocds.web.rest.controller;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;
import static org.springframework.data.mongodb.core.query.Criteria.where;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.ArrayUtils;
import org.bson.types.ObjectId;
import org.devgateway.ocds.web.rest.controller.request.DefaultFilterPagingRequest;
import org.devgateway.ocds.web.rest.controller.request.GroupingFilterPagingRequest;
import org.devgateway.ocds.web.rest.controller.request.YearFilterPagingRequest;
import org.devgateway.toolkit.persistence.mongo.aggregate.CustomSortingOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.query.Criteria;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

/**
 * @author mpostelnicu
 *
 */
public abstract class GenericOCDSController {

    private static final int LAST_DAY = 31;

    private static final int LAST_MONTH_ZERO = 11;

    protected Map<String, Object> filterProjectMap;

    protected final Logger logger = LoggerFactory.getLogger(GenericOCDSController.class);

    @Autowired
    protected MongoTemplate mongoTemplate;

    /**
     * Gets the date of the first day of the year (01.01.year)
     *
     * @param year
     * @return
     */
    protected Date getStartDate(final int year) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.DAY_OF_YEAR, 1);
        Date start = cal.getTime();
        return start;
    }

    /**
     * Gets the date of the last date of the year (31.12.year)
     *
     * @param year
     * @return
     */
    protected Date getEndDate(final int year) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, LAST_MONTH_ZERO);
        cal.set(Calendar.DAY_OF_MONTH, LAST_DAY);
        Date end = cal.getTime();
        return end;
    }

    /**
     * Appends the procuring bid type id for this filter, this will fitler based
     * on tender.items.classification._id
     *
     * @param filter
     * @return the {@link Criteria} for this filter
     */
    protected Criteria getBidTypeIdFilterCriteria(final DefaultFilterPagingRequest filter) {
        return createFilterCriteria("tender.items.classification._id", filter.getBidTypeId(), filter);
    }
    
    /**
     * Adds monthly projection operation, when needed, if the
     * {@link YearFilterPagingRequest#getMonthly()}
     * 
     * @param filter
     * @param project
     * @param field
     */
    protected void addYearlyMonthlyProjection(YearFilterPagingRequest filter, DBObject project, String field) {
        project.put("year", new BasicDBObject("$year", field));
        if (filter.getMonthly()) {
            project.put(("month"), new BasicDBObject("$month", field));
        }
    }
    
    protected CustomSortingOperation getSortByYear() {
        return new CustomSortingOperation(new BasicDBObject("year", 1));
    }
    
    protected void addYearlyMonthlyReferenceToGroup(YearFilterPagingRequest filter, DBObject group) {
        if (filter.getMonthly()) {
            group.put(Fields.UNDERSCORE_ID, new BasicDBObject("year", "$year").append("month", "$month"));            
        } else {
            group.put(Fields.UNDERSCORE_ID, "$year");
        }
    }

    /**
     * Returns the grouping fields based on the {@link YearFilterPagingRequest#getMonthly()} setting
     * 
     * @param filter
     * @return
     */
    protected String[] getYearlyMonthlyGroupingFields(YearFilterPagingRequest filter) {
        if (filter.getMonthly()) {
            return new String[] { "$year", "$month" };
        } else {
            return new String[] { "$year" };
        }
    }
    
    /**
     * @see #getYearlyMonthlyGroupingFields(YearFilterPagingRequest)
     * 
     * @param filter
     * @param extraGroups adds extra groups
     * @return
     */
    protected String[] getYearlyMonthlyGroupingFields(YearFilterPagingRequest filter, String... extraGroups) {
        return ArrayUtils.addAll(getYearlyMonthlyGroupingFields(filter), extraGroups);
    }
    
    protected GroupOperation getYearlyMonthlyGroupingOperation(YearFilterPagingRequest filter) {
        return group(getYearlyMonthlyGroupingFields(filter));
    }
    
    protected ProjectionOperation transformYearlyGrouping(YearFilterPagingRequest filter) {
        if (filter.getMonthly()) {
            return project();
        } else {
            return project(Fields.from(Fields.field("year", Fields.UNDERSCORE_ID_REF)))
                    .andExclude(Fields.UNDERSCORE_ID);
        }
    }
    
    protected void addYearlyMonthlyGroupingOperationFirst(YearFilterPagingRequest filter, DBObject group) {
        group.put("year", new BasicDBObject("$first", "$year"));
        if (filter.getMonthly()) {
            group.put("month", new BasicDBObject("$first", "$month"));
        }
    }


    /**
     * Appends the tender.items.deliveryLocation._id
     *
     * @param filter
     * @return the {@link Criteria} for this filter
     */
    protected Criteria getByTenderDeliveryLocationIdentifier(final DefaultFilterPagingRequest filter) {
        return createFilterCriteria("tender.items.deliveryLocation._id",
                filter.getTenderLoc(), filter);
    }

    /**
     * Creates a search criteria filter based on tender.value.amount and uses
     * {@link DefaultFilterPagingRequest#getMinTenderValue()} and
     * {@link DefaultFilterPagingRequest#getMaxTenderValue()} to create
     * interval search
     *
     * @param filter
     * @return
     */
    private Criteria getByTenderAmountIntervalCriteria(final DefaultFilterPagingRequest filter) {
        if (filter.getMaxTenderValue() == null && filter.getMinTenderValue() == null) {
            return new Criteria();
        }
        Criteria criteria = where("tender.value.amount");
        if (filter.getMinTenderValue() != null) {
            if (filter.getInvert()) {
                criteria = criteria.not();
            }
            criteria = criteria.gte(filter.getMinTenderValue().doubleValue());
        }
        if (filter.getMaxTenderValue() != null) {
            if (filter.getInvert()) {
                criteria = criteria.not();
            }
            criteria = criteria.lte(filter.getMaxTenderValue().doubleValue());
        }
        return criteria;
    }

    /**
     * Creates a search criteria filter based on awards.value.amount and uses
     * {@link DefaultFilterPagingRequest#getMinAwardValue()} and
     * {@link DefaultFilterPagingRequest#getMaxAwardValue()} to create
     * interval search
     *
     * @param filter
     * @return
     */
    private Criteria getByAwardAmountIntervalCriteria(final DefaultFilterPagingRequest filter) {
        if (filter.getMaxAwardValue() == null && filter.getMinAwardValue() == null) {
            return new Criteria();
        }
        Criteria criteria = where("awards.value.amount");
        if (filter.getMinAwardValue() != null) {
            if (filter.getInvert()) {
                criteria = criteria.not();
            }
            criteria = criteria.gte(filter.getMinAwardValue().doubleValue());
        }
        if (filter.getMaxAwardValue() != null) {
            if (filter.getInvert()) {
                criteria = criteria.not();
            }
            criteria = criteria.lte(filter.getMaxAwardValue().doubleValue());
        }
        return criteria;
    }


    /**
     * Appends the contrMethod filter, based on tender.contrMethod
     *
     * @param filter
     * @return the {@link Criteria} for this filter
     */
    protected Criteria getContrMethodFilterCriteria(final DefaultFilterPagingRequest filter) {
        return filter.getContrMethod() == null ? new Criteria()
                : createFilterCriteria("tender.contrMethod._id",
                filter.getContrMethod().stream().map(s -> new ObjectId(s)).collect(Collectors.toList()),
                filter);
    }


    private <S> Criteria createFilterCriteria(final String filterName, final List<S> filterValues,
                                              final DefaultFilterPagingRequest filter) {
        if (filterValues == null) {
            return new Criteria();
        }
        return filter.getInvert() ? where(filterName).not().in(filterValues.toArray())
                : where(filterName).in(filterValues.toArray());
    }

    /**
     * Appends the procuring entity id for this filter, this will fitler based
     * on tender.procuringEntity._id
     *
     * @param filter
     * @return the {@link Criteria} for this filter
     */
    protected Criteria getProcuringEntityIdCriteria(final DefaultFilterPagingRequest filter) {
        return createFilterCriteria("tender.procuringEntity._id", filter.getProcuringEntityId(), filter);
    }


    /**
     * Appends the supplier entity id for this filter, this will fitler based
     * on tender.procuringEntity._id
     *
     * @param filter
     * @return the {@link Criteria} for this filter
     */
    protected Criteria getSupplierIdCriteria(final DefaultFilterPagingRequest filter) {
        return createFilterCriteria("awards.suppliers._id", filter.getSupplierId(), filter);
    }

    @PostConstruct
    protected void init() {
        Map<String, Object> tmpMap = new HashMap<>();
        tmpMap.put("tender.procuringEntity._id", 1);
        tmpMap.put("awards.suppliers._id", 1);
        tmpMap.put("tender.items.classification._id", 1);
        tmpMap.put("tender.items.deliveryLocation._id", 1);
        tmpMap.put("tender.procurementMethodDetails", 1);
        tmpMap.put("tender.contrMethod", 1);
        tmpMap.put("tender.value.amount", 1);
        tmpMap.put("awards.value.amount", 1);

        filterProjectMap = Collections.unmodifiableMap(tmpMap);
    }

    protected Criteria getYearFilterCriteria(final YearFilterPagingRequest filter, final String dateProperty) {
        Criteria[] yearCriteria = null;
        Criteria criteria = new Criteria();

        if (filter.getYear() == null) {
            yearCriteria = new Criteria[1];
            yearCriteria[0] = new Criteria();
        } else {
            yearCriteria = new Criteria[filter.getYear().size()];
            for (int i = 0; i < filter.getYear().size(); i++) {
                yearCriteria[i] = where(dateProperty).gte(getStartDate(filter.getYear().get(i)))
                        .lte(getEndDate(filter.getYear().get(i)));
            }
        }

        return filter.getInvert() ? criteria.norOperator(yearCriteria) : criteria.orOperator(yearCriteria);
    }

    /**
     * Appends the bid selection method to the filter, this will filter based on
     * tender.procurementMethodDetails. It accepts multiple elements
     *
     * @param filter
     * @return the {@link Criteria} for this filter
     */
    protected Criteria getBidSelectionMethod(final DefaultFilterPagingRequest filter) {
        return createFilterCriteria("tender.procurementMethodDetails", filter.getBidSelectionMethod(), filter);
    }

    protected Criteria getDefaultFilterCriteria(final DefaultFilterPagingRequest filter) {
        return new Criteria().andOperator(getBidTypeIdFilterCriteria(filter), getProcuringEntityIdCriteria(filter),
                getBidSelectionMethod(filter), getContrMethodFilterCriteria(filter),
                getSupplierIdCriteria(filter),
                getByTenderDeliveryLocationIdentifier(filter), getByTenderAmountIntervalCriteria(filter),
                getByAwardAmountIntervalCriteria(filter));
    }

    protected Criteria getYearDefaultFilterCriteria(final YearFilterPagingRequest filter, final String dateProperty) {
        return new Criteria().andOperator(getBidTypeIdFilterCriteria(filter), getProcuringEntityIdCriteria(filter),
                getSupplierIdCriteria(filter), getByTenderDeliveryLocationIdentifier(filter),
                getByTenderAmountIntervalCriteria(filter), getByAwardAmountIntervalCriteria(filter),
                getYearFilterCriteria(filter, dateProperty));
    }

    protected MatchOperation getMatchDefaultFilterOperation(final DefaultFilterPagingRequest filter) {
        return match(getDefaultFilterCriteria(filter));
    }

    /**
     * Creates a groupby expression that takes into account the filter. It will
     * only use one of the filter options as groupby and ignores the rest.
     *
     * @param filter
     * @param existingGroupBy
     * @return
     */
    protected GroupOperation getTopXFilterOperation(final GroupingFilterPagingRequest filter,
                                                    final String... existingGroupBy) {
        List<String> groupBy = new ArrayList<>();
        if (filter.getGroupByCategory() == null) {
            groupBy.addAll(Arrays.asList(existingGroupBy));
        }

        if (filter.getGroupByCategory() != null) {
            groupBy.add(getGroupByCategory(filter));
        }

        return group(groupBy.toArray(new String[0]));
    }

    private String getGroupByCategory(final GroupingFilterPagingRequest filter) {
        if ("bidSelectionMethod".equals(filter.getGroupByCategory())) {
            return "tender.procurementMethodDetails".replace(".", "");
        } else if ("bidTypeId".equals(filter.getGroupByCategory())) {
            return "tender.items.classification._id".replace(".", "");
        } else if ("procuringEntityId".equals(filter.getGroupByCategory())) {
            return "tender.procuringEntity._id".replace(".", "");
        }
        return null;
    }

}
