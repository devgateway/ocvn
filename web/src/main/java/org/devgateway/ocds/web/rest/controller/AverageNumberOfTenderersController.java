/*******************************************************************************
 * Copyright (c) 2015 Development Gateway, Inc and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the MIT License (MIT)
 * which accompanies this distribution, and is available at
 * https://opensource.org/licenses/MIT
 *
 * Contributors:
 * Development Gateway - initial API and implementation
 *******************************************************************************/
package org.devgateway.ocds.web.rest.controller;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.limit;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.skip;
import static org.springframework.data.mongodb.core.query.Criteria.where;

import java.util.List;

import javax.validation.Valid;

import org.devgateway.ocds.web.rest.controller.request.YearFilterPagingRequest;
import org.devgateway.toolkit.persistence.mongo.aggregate.CustomProjectionOperation;
import org.devgateway.toolkit.persistence.mongo.aggregate.CustomSortingOperation;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import io.swagger.annotations.ApiOperation;

/**
 *
 * @author mpostelnicu
 *
 */
@RestController
@CacheConfig(keyGenerator = "genericPagingRequestKeyGenerator", cacheNames = "genericPagingRequestJson")
@Cacheable
public class AverageNumberOfTenderersController extends GenericOCDSController {

    public static final class Keys {
        public static final String AVERAGE_NO_OF_TENDERERS = "averageNoTenderers";
        public static final String YEAR = "year";
    }

    @ApiOperation(value = "Calculate average number of tenderers, by year. The endpoint can be filtered"
            + "by year read from tender.tenderPeriod.startDate. "
            + "The number of tenderers are read from tender.numberOfTenderers")

    @RequestMapping(value = "/api/averageNumberOfTenderers",
            method = { RequestMethod.POST, RequestMethod.GET }, produces = "application/json")
    public List<DBObject> averageNumberOfTenderers(@ModelAttribute @Valid final YearFilterPagingRequest filter) {

        DBObject project = new BasicDBObject();
        project.put("year", new BasicDBObject("$year", "$tender.tenderPeriod.startDate"));
        project.put("tender.numberOfTenderers", 1);

        Aggregation agg = newAggregation(
                match(where("tender.numberOfTenderers").gt(0).and("tender.tenderPeriod.startDate").exists(true)
                        .andOperator(getYearDefaultFilterCriteria(filter, "tender.tenderPeriod.startDate"))),
                new CustomProjectionOperation(project),
                group("$year").avg("tender.numberOfTenderers").as(Keys.AVERAGE_NO_OF_TENDERERS),
                project(Fields.from(Fields.field("year", Fields.UNDERSCORE_ID_REF)))
                        .andInclude(Keys.AVERAGE_NO_OF_TENDERERS)
                        .andExclude(Fields.UNDERSCORE_ID),
                new CustomSortingOperation(new BasicDBObject(Keys.YEAR, 1)),
                skip(filter.getSkip()), limit(filter.getPageSize()));

        AggregationResults<DBObject> results = mongoTemplate.aggregate(agg, "release", DBObject.class);
        List<DBObject> list = results.getMappedResults();
        return list;
    }

}