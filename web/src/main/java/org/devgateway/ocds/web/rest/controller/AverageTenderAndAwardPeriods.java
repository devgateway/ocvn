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
import java.util.Arrays;
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.limit;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
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
public class AverageTenderAndAwardPeriods extends GenericOCDSController {

	private static final int DAY_MS = 86400000;

	@ApiOperation(value = "Calculates the average tender period, per each year. The year is taken from "
			+ "tender.tenderPeriod.startDate and the duration is taken by counting the days"
			+ "between tender.tenderPeriod.endDate and tender.tenderPeriod.startDate")
	@RequestMapping(value = "/api/averageTenderPeriod", method = RequestMethod.GET, produces = "application/json")
	public List<DBObject> averageTenderPeriod(@ModelAttribute @Valid final DefaultFilterPagingRequest filter) {

		DBObject year = new BasicDBObject("$year", "$tender.tenderPeriod.startDate");

		DBObject tenderLengthDays = new BasicDBObject("$divide",
				Arrays.asList(
						new BasicDBObject("$subtract",
								Arrays.asList("$tender.tenderPeriod.endDate", "$tender.tenderPeriod.startDate")),
						DAY_MS));

		DBObject project = new BasicDBObject();
		project.put(Fields.UNDERSCORE_ID, 0);
		project.put("year", year);
		project.put("tenderLengthDays", tenderLengthDays);

		Aggregation agg = newAggregation(
				match(where("tender.tenderPeriod.startDate").exists(true).and("tender.tenderPeriod.endDate")
						.exists(true).andOperator(getDefaultFilterCriteria(filter))),
				new CustomOperation(new BasicDBObject("$project", project)),
				group("$year").avg("$tenderLengthDays").as("averageTenderDays"),
				sort(Direction.DESC, "averageTenderDays"), skip(filter.getSkip()), limit(filter.getPageSize()));

		AggregationResults<DBObject> results = mongoTemplate.aggregate(agg, "release", DBObject.class);
		List<DBObject> list = results.getMappedResults();
		return list;
	}

	@ApiOperation(value = "Calculates the average award period, per each year. The year is taken from "
			+ "awards.date and the duration is taken by counting the days"
			+ "between tender.tenderPeriod.endDate and tender.tenderPeriod.startDate. The award has to be active.")
	@RequestMapping(value = "/api/averageAwardPeriod", method = RequestMethod.GET, produces = "application/json")
	public List<DBObject> averageAwardPeriod(@ModelAttribute @Valid final DefaultFilterPagingRequest filter) {
		DBObject year = new BasicDBObject("$year", "$awards.date");

		DBObject awardLengthDays = new BasicDBObject("$divide", Arrays.asList(
				new BasicDBObject("$subtract", Arrays.asList("$awards.date", "$tender.tenderPeriod.endDate")), DAY_MS));

		DBObject project = new BasicDBObject();
		project.put(Fields.UNDERSCORE_ID, 0);
		project.put("year", year);
		project.put("awardLengthDays", awardLengthDays);
		project.put("awards.date", 1);
		project.put("awards.status", 1);
		project.put("tender.tenderPeriod.endDate", 1);

		DBObject group = new BasicDBObject();
		group.put(Fields.UNDERSCORE_ID, "$year");
		group.put("averageAwardDays", new BasicDBObject("$avg", "$awardLengthDays"));

		DBObject sort = new BasicDBObject();
		sort.put("averageAwardDays", -1);

		Aggregation agg = newAggregation(
				// this is repeated so we gain speed by filtering items before
				// unwind
				match(where("tender.tenderPeriod.endDate").exists(true).and("awards.date").exists(true)
						.and("awards.status").is("active")),
				unwind("$awards"),
				// we need to filter the awards again after unwind
				match(where("awards.date").exists(true).and("awards.status").is("active")
						.andOperator(getDefaultFilterCriteria(filter))),
				new CustomOperation(new BasicDBObject("$project", project)),
				new CustomOperation(new BasicDBObject("$group", group)),
				new CustomOperation(new BasicDBObject("$sort", sort)), skip(filter.getSkip()),
				limit(filter.getPageSize()));

		AggregationResults<DBObject> results = mongoTemplate.aggregate(agg, "release", DBObject.class);
		List<DBObject> list = results.getMappedResults();
		return list;
	}

}