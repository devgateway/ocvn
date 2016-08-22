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

import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.unwind;
import static org.springframework.data.mongodb.core.query.Criteria.where;

import java.util.List;

import javax.validation.Valid;

import org.devgateway.ocds.web.rest.controller.request.DefaultFilterPagingRequest;
import org.devgateway.toolkit.persistence.mongo.aggregate.CustomProjectionOperation;
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
public class NumberOfTendersByItemClassification extends GenericOCDSController {

	public static final class Keys {
		public static final String TOTAL_TENDERS = "totalTenders";
		public static final String ITEMS_CLASSIFICATION = "items.classification";
		public static final String YEAR = "year";
	}
	
	@ApiOperation(value = "This should show the number of tenders per tender.items.classification."
			+ "The tender date is taken from tender.tenderPeriod.startDate.")
	@RequestMapping(value = "/api/numberOfTendersByItemClassification", method = { RequestMethod.POST,
			RequestMethod.GET }, produces = "application/json")
	public List<DBObject> numberOfTendersByItemClassification(
			@ModelAttribute @Valid final DefaultFilterPagingRequest filter) {

        DBObject year = new BasicDBObject("$year", "$tender.tenderPeriod.startDate");

        DBObject project = new BasicDBObject();
        project.put(Fields.UNDERSCORE_ID, 0);
        project.put("year", year);
		project.put("tender." + Keys.ITEMS_CLASSIFICATION, 1);

		Aggregation agg = newAggregation(
				match(where("tender.tenderPeriod.startDate").exists(true)),
				getMatchDefaultFilterOperation(filter), new CustomProjectionOperation(project),
				unwind("tender.items"),
				group("$year", "$tender." + Keys.ITEMS_CLASSIFICATION).count().as(Keys.TOTAL_TENDERS),
				sort(Direction.ASC, Fields.UNDERSCORE_ID));

        AggregationResults<DBObject> results = mongoTemplate.aggregate(agg, "release", DBObject.class);
        List<DBObject> list = results.getMappedResults();
        return list;
    }

}