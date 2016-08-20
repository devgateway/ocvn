package org.devgateway.ocds.web.rest.controller;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.limit;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.skip;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;
import static org.springframework.data.mongodb.core.query.Criteria.where;

import java.util.List;

import javax.validation.Valid;

import org.devgateway.ocds.web.rest.controller.request.YearFilterPagingRequest;
import org.devgateway.toolkit.persistence.mongo.aggregate.CustomProjectionOperation;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
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
public class TenderPriceByTypeYearController extends GenericOCDSController {

	@ApiOperation(value = "Returns the tender price by OCDS type (procurementMethod), by year. "
			+ "The OCDS type is read from tender.procurementMethod. The tender price is read from "
			+ "tender.value.amount")
	@RequestMapping(value = "/api/tenderPriceByProcurementMethodYear", method = { RequestMethod.POST,
			RequestMethod.GET }, produces = "application/json")
	public List<DBObject> tenderPriceByProcurementMethodYear(
			@ModelAttribute @Valid final YearFilterPagingRequest filter) {

		DBObject project = new BasicDBObject();
		project.put("tender.procurementMethod", 1);
		project.put("tender.value", 1);

		Aggregation agg = newAggregation(
                match(where("awards").elemMatch(where("status").is("active")).and("tender.value").exists(true).
                		andOperator(getYearFilterCriteria("tender.tenderPeriod.startDate", filter))),
				match(getDefaultFilterCriteria(filter)),
				new CustomProjectionOperation(project),
				group("tender.procurementMethod").sum("$tender.value.amount").as("totalTenderAmount"),
				sort(Direction.DESC, "totalTenderAmount"), skip(filter.getSkip()), limit(filter.getPageSize()));

		AggregationResults<DBObject> results = mongoTemplate.aggregate(agg, "release", DBObject.class);
		List<DBObject> tagCount = results.getMappedResults();
		return tagCount;

	}

	@ApiOperation(value = "Returns the tender price by Vietnam type (procurementMethodDetails), by year. "
			+ "The OCDS type is read from tender.procurementMethodDetails. The tender price is read from "
			+ "tender.value.amount")
	@RequestMapping(value = "/api/tenderPriceByBidSelectionMethodYear", method = { RequestMethod.POST,
			RequestMethod.GET }, produces = "application/json")
	public List<DBObject> tenderPriceByBidSelectionMethodYear(
			@ModelAttribute @Valid final YearFilterPagingRequest filter) {

		DBObject project = new BasicDBObject();
		project.put("tender.procurementMethodDetails", 1);
		project.put("tender.value", 1);

		Aggregation agg = newAggregation(
				match(where("awards").elemMatch(where("status").is("active")).and("tender.value").exists(true).
				andOperator(getYearFilterCriteria("tender.tenderPeriod.startDate", filter))),
				getMatchDefaultFilterOperation(filter), new CustomProjectionOperation(project),
				group("year", "tender.procurementMethodDetails").sum("$tender.value.amount").as("totalTenderAmount"),
				sort(Direction.ASC, "year"));

		AggregationResults<DBObject> results = mongoTemplate.aggregate(agg, "release", DBObject.class);
		List<DBObject> tagCount = results.getMappedResults();
		return tagCount;

	}

}
