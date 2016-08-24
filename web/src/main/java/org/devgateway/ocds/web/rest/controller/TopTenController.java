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

import static org.springframework.data.mongodb.core.aggregation.Aggregation.limit;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.unwind;
import static org.springframework.data.mongodb.core.query.Criteria.where;

import java.util.List;

import javax.validation.Valid;

import org.devgateway.ocds.web.rest.controller.request.YearFilterPagingRequest;
import org.devgateway.toolkit.persistence.mongo.aggregate.CustomOperation;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort.Direction;
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
public class TopTenController extends GenericOCDSController {

    public static final class Keys {
        public static final String AWARDS = "awards";
        public static final String DATE = "date";
        public static final String SUPPLIERS = "suppliers";
        public static final String VALUE = "value";
        public static final String NAME = "name";
        public static final String TENDER = "tender";
        public static final String TENDER_PERIOD = "tenderPeriod";
        public static final String PROCURING_ENTITY = "procuringEntity";
    }

    /**
     * db.release.aggregate( [ {$match: {"awards.value.amount": {$exists:
     * true}}}, {$unwind:"$awards"},
     * {$project:{_id:0,"awards.date":1,
     * "awards.suppliers.name":1,"awards.value":1, "planning.budget":1}},
     * {$sort: {"awards.value.amount":-1}}, {$limit:10} ] )
     *
     * @return
     */

    @ApiOperation(value = "Returns the top ten largest active awards."
            + " The amount is taken from the award.value field. The returned data will contain"
            + "the following fields: "
            + "awards.date, awards.suppliers.name, "
            + "awards.value.amount, awards.suppliers.name, planning.budget (if any)")
    @RequestMapping(value = "/api/topTenLargestAwards", method = { RequestMethod.POST,
            RequestMethod.GET },
            produces = "application/json")
    public List<DBObject> topTenLargestAwards(@ModelAttribute @Valid final YearFilterPagingRequest filter) {

        BasicDBObject project = new BasicDBObject();
        project.put(Fields.UNDERSCORE_ID, 0);
        project.put("awards.date", 1);
        project.put("awards.suppliers.name", 1);
        project.put("awards.value.amount", 1);
        project.put("planning.budget", 1);

        Aggregation agg = newAggregation(
                match(where("awards.value.amount").exists(true).and("awards.status").is("active")
                        .andOperator(getDefaultFilterCriteria(filter))),
                unwind("$awards"), match(getYearFilterCriteria("awards.date", filter)),
                new CustomOperation(new BasicDBObject("$project", project)),
                sort(Direction.DESC, "awards.value.amount"), limit(10));

        AggregationResults<DBObject> results = mongoTemplate.aggregate(agg, "release", DBObject.class);
        List<DBObject> tagCount = results.getMappedResults();
        return tagCount;

    }

    /**
     * db.release.aggregate( [ {$match: {"tender.value.amount": {$exists:
     * true}}}, {$project:{_id:0,"tender.value":1,
     * "tender.tenderPeriod":1,"tender.procuringEntity.name":1}}, {$sort:
     * {"tender.value.amount":-1}}, {$limit:10} ] )
     *
     * @return
     */
    @ApiOperation(value = "Returns the top ten largest active tenders."
            + " The amount is taken from the tender.value.amount field." + " The returned data will contain"
            + "the following fields: " + "tender.date, tender.value.amount, tender.tenderPeriod, "
            + "tender.procuringEntity.name")
    @RequestMapping(value = "/api/topTenLargestTenders", method = { RequestMethod.POST, RequestMethod.GET },
            produces = "application/json")
    public List<DBObject> topTenLargestTenders(@ModelAttribute @Valid final YearFilterPagingRequest filter) {

        BasicDBObject project = new BasicDBObject();
        project.put(Fields.UNDERSCORE_ID, 0);
        project.put("tender.value.amount", 1);
        project.put("tender.tenderPeriod", 1);
        project.put("tender.procuringEntity.name", 1);

        Aggregation agg = newAggregation(
                match(where("tender.value.amount").exists(true).andOperator(getDefaultFilterCriteria(filter))),
                match(getYearFilterCriteria("tender.tenderPeriod.startDate", filter)),
                new CustomOperation(new BasicDBObject("$project", project)),
                sort(Direction.DESC, "tender.value.amount"), limit(10));

        AggregationResults<DBObject> results = mongoTemplate.aggregate(agg, "release", DBObject.class);
        List<DBObject> tagCount = results.getMappedResults();
        return tagCount;

    }

}