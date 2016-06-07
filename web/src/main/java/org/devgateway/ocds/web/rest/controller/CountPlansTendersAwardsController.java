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

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import io.swagger.annotations.ApiOperation;

import org.devgateway.ocds.web.rest.controller.request.DefaultFilterPagingRequest;
import org.devgateway.toolkit.persistence.mongo.aggregate.CustomOperation;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.limit;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.skip;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.unwind;
import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 *
 * @author mpostelnicu
 *
 */
@RestController
public class CountPlansTendersAwardsController extends GenericOCDSController {

    /**
     * db.release.aggregate( [ {$match : { "planning.bidPlanProjectDateApprove":
     * { $exists: true } }}, {$project: { planning:1, year: {$year :
     * "$planning.bidPlanProjectDateApprove"} } }, {$group: {_id: "$year",
     * count: { $sum:1}}}, {$sort: { _id:1}} ])
     *
     * @return
     */
	@ApiOperation(value = "Count of bid plans, by year. This will count the releases that have the field"
			+ "planning.bidPlanProjectDateApprove populated. "
			+ "The year grouping is taken from planning.bidPlanProjectDateApprove")
    @RequestMapping(value = "/api/countBidPlansByYear", method = RequestMethod.GET, produces = "application/json")
    public List<DBObject> countBidPlansByYear(@ModelAttribute @Valid final DefaultFilterPagingRequest filter) {

        DBObject project = new BasicDBObject();
        project.put("year", new BasicDBObject("$year", "$planning.bidPlanProjectDateApprove"));

        Aggregation agg = Aggregation.newAggregation(match(where("planning.bidPlanProjectDateApprove").exists(true)),
                getMatchDefaultFilterOperation(filter), new CustomOperation(new BasicDBObject("$project", project)),
                group("$year").count().as("count"), sort(Direction.DESC, Fields.UNDERSCORE_ID), skip(filter.getSkip()),
                limit(filter.getPageSize()));

        AggregationResults<DBObject> results = mongoTemplate.aggregate(agg, "release", DBObject.class);
        List<DBObject> tagCount = results.getMappedResults();
        return tagCount;

    }

    /**
     * db.release.aggregate( [ {$match : { "tender.tenderPeriod.startDate": {
     * $exists: true } }}, {$project: { year: {$year :
     * "$tender.tenderPeriod.startDate"} } }, {$group: {_id: "$year", count: {
     * $sum:1}}}, {$sort: { _id:1}} ])
     *
     * @return
     */
	@ApiOperation(value = "Count the tenders and group the results by year. The year is calculated from "
			+ "tender.tenderPeriod.startDate.")
    @RequestMapping(value = "/api/countTendersByYear", method = RequestMethod.GET, produces = "application/json")
    public List<DBObject> countTendersByYear(@ModelAttribute @Valid final DefaultFilterPagingRequest filter) {

        DBObject project = new BasicDBObject();
        project.put("year", new BasicDBObject("$year", "$tender.tenderPeriod.startDate"));

        Aggregation agg = Aggregation.newAggregation(match(where("tender.tenderPeriod.startDate").exists(true)),
                getMatchDefaultFilterOperation(filter), new CustomOperation(new BasicDBObject("$project", project)),
                group("$year").count().as("count"), sort(Direction.DESC, Fields.UNDERSCORE_ID), skip(filter.getSkip()),
                limit(filter.getPageSize()));

        AggregationResults<DBObject> results = mongoTemplate.aggregate(agg, "release", DBObject.class);
        List<DBObject> tagCount = results.getMappedResults();
        return tagCount;
    }

    /**
     * db.release.aggregate( [ {$match : { "awards.0": { $exists: true } }},
     * {$project: {awards:1}}, {$unwind: "$awards"}, {$match: {"awards.date":
     * {$exists:true}}}, {$project: { year: {$year : "$awards.date"} } },
     * {$group: {_id: "$year", count: { $sum:1}}}, {$sort: { _id:1}} ])
     *
     * @return
     */
	@ApiOperation(value = "Count the awards and group the results by year. "
			+ "The year is calculated from the awards.date field.")
    @RequestMapping(value = "/api/countAwardsByYear", method = RequestMethod.GET, produces = "application/json")
    public List<DBObject> countAwardsByYear(@ModelAttribute @Valid final DefaultFilterPagingRequest filter) {

        DBObject project0 = new BasicDBObject();
        project0.put("awards", 1);

        DBObject project = new BasicDBObject();
        project.put("year", new BasicDBObject("$year", "$awards.date"));
        project.put(Fields.UNDERSCORE_ID, 0);

        DBObject group = new BasicDBObject();
        group.put(Fields.UNDERSCORE_ID, "$year");
        group.put("count", new BasicDBObject("$sum", 1));

        DBObject sort = new BasicDBObject();
        sort.put("count", -1);

        Aggregation agg = Aggregation.newAggregation(match(where("awards.0").exists(true)),
				getMatchDefaultFilterOperation(filter), new CustomOperation(new BasicDBObject("$project", project0)),
				unwind("$awards"), match(where("awards.date").exists(true)),
				new CustomOperation(new BasicDBObject("$project", project)),
                new CustomOperation(new BasicDBObject("$group", group)),
                new CustomOperation(new BasicDBObject("$sort", sort)), skip(filter.getSkip()),
                limit(filter.getPageSize()));

        AggregationResults<DBObject> results = mongoTemplate.aggregate(agg, "release", DBObject.class);
        List<DBObject> tagCount = results.getMappedResults();
        return tagCount;
    }
}