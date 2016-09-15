/**
 * 
 */
package org.devgateway.ocds.web.rest.controller;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import io.swagger.annotations.ApiOperation;
import org.devgateway.toolkit.persistence.mongo.aggregate.CustomGroupingOperation;
import org.devgateway.toolkit.persistence.mongo.aggregate.CustomProjectionOperation;
import org.devgateway.toolkit.persistence.mongo.aggregate.CustomSortingOperation;
import org.devgateway.toolkit.persistence.mongo.aggregate.CustomUnwindOperation;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;
import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * @author mihai
 *
 */

@Cacheable
@RestController
@CacheConfig(cacheNames = "tendersAwardsYears")
public class TendersAwardsYears extends GenericOCDSController {

	@ApiOperation(value = "")
	@RequestMapping(value = "/api/tendersAwardsYears", method = { RequestMethod.POST,
			RequestMethod.GET }, produces = "application/json")
	public List<DBObject> tendersAwardsYears() {

		BasicDBObject project1 = new BasicDBObject();

		project1.put("tenderYear",
				new BasicDBObject("$cond",
						Arrays.asList(new BasicDBObject("$gt", Arrays.asList("$tender.tenderPeriod.startDate", null)),
								new BasicDBObject("$year", "$tender.tenderPeriod.startDate"), null)));

		project1.put("awardYear",
				new BasicDBObject("$cond", Arrays.asList(new BasicDBObject("$gt", Arrays.asList("$awards.date", null)),
						new BasicDBObject("$year", "$awards.date"), null)));
		project1.put(Fields.UNDERSCORE_ID, 0);

		BasicDBObject project2 = new BasicDBObject();
		project2.put("year", Arrays.asList("$tenderYear", "$awardYear"));

		Aggregation agg = Aggregation.newAggregation(
				project().and("tender.tenderPeriod.startDate").as("tender.tenderPeriod.startDate").and("awards.date")
						.as("awards.date"),
				match(new Criteria().orOperator(where("tender.tenderPeriod.startDate").exists(true),
						where("awards.date").exists(true))),
				new CustomUnwindOperation("$awards"), new CustomProjectionOperation(project1),
				new CustomProjectionOperation(project2), new CustomUnwindOperation("$year"),
				match(where("year").ne(null)),
				new CustomGroupingOperation(new BasicDBObject(Fields.UNDERSCORE_ID, "$year")),
				new CustomSortingOperation(new BasicDBObject(Fields.UNDERSCORE_ID, 1)));

		AggregationResults<DBObject> results = mongoTemplate.aggregate(agg, "release", DBObject.class);
		List<DBObject> tagCount = results.getMappedResults();
		return tagCount;
	}

}