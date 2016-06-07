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
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;
import static org.springframework.data.mongodb.core.query.Criteria.where;

import java.util.List;

import javax.validation.Valid;

import org.devgateway.ocds.web.rest.controller.request.DefaultFilterPagingRequest;
import org.devgateway.toolkit.persistence.mongo.aggregate.CustomProjectionOperation;
import org.devgateway.toolkit.persistence.mongo.aggregate.CustomSortingOperation;
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
public class AverageNumberOfTenderers extends GenericOCDSController {

	@ApiOperation(value = "Calculate average number of tenderers, by year. The endpoint can be filtered"
			+ "by year read from tender.tenderPeriod.startDate. "
			+ "The number of tenderers are read from tender.numberOfTenderers")
	@RequestMapping(value = "/api/averageNumberOfTenderers", method = RequestMethod.GET, produces = "application/json")
	public List<DBObject> averageNumberOfTenderers(@ModelAttribute @Valid final DefaultFilterPagingRequest filter) {

		DBObject project = new BasicDBObject();
		project.put("year", new BasicDBObject("$year", "$tender.tenderPeriod.startDate"));
		project.put("tender.numberOfTenderers", 1);

		Aggregation agg = newAggregation(
				match(where("tender.numberOfTenderers").gt(0).andOperator(getDefaultFilterCriteria(filter))),
				new CustomProjectionOperation(project),
				group("$year").avg("tender.numberOfTenderers").as("averageNoTenderers"),
				project(Fields.from(Fields.field("year", Fields.UNDERSCORE_ID_REF))).andInclude("averageNoTenderers")
						.andExclude(Fields.UNDERSCORE_ID),
				new CustomSortingOperation(new BasicDBObject("year", 1)), sort(Direction.ASC, "year"),
				skip(filter.getSkip()), limit(filter.getPageSize()));

		AggregationResults<DBObject> results = mongoTemplate.aggregate(agg, "release", DBObject.class);
		List<DBObject> list = results.getMappedResults();
		return list;
	}

}