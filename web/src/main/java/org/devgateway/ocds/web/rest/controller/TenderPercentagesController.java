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
import static org.springframework.data.mongodb.core.aggregation.Aggregation.skip;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.unwind;
import static org.springframework.data.mongodb.core.query.Criteria.where;

import java.util.Arrays;
import java.util.List;

import javax.validation.Valid;

import org.devgateway.ocds.persistence.mongo.Tender;
import org.devgateway.ocds.web.rest.controller.request.DefaultFilterPagingRequest;
import org.devgateway.toolkit.persistence.mongo.aggregate.CustomGroupingOperation;
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
public class TenderPercentagesController extends GenericOCDSController {

	


	public static final class Keys {
		public static final String TOTAL_TENDERS = "totalTenders";
		public static final String TOTAL_CANCELLED = "totalCancelled";
		public static final String PERCENT_CANCELLED = "percentCancelled";
		public static final String YEAR = "year";
		public static final String TOTAL_TENDERS_WITH_TWO_OR_MORE_TENDERERS = "totalTendersWithTwoOrMoreTenderers";
		public static final String PERCENT_TENDERS = "percentTenders";
		public static final String TOTAL_TENDERS_WITH_ONE_OR_MORE_TENDERERS = "totalTendersWithOneOrMoreTenderers";
		public static final String TOTAL_TENDERS_USING_EBID = "totalTendersUsingEbid";
		public static final String PERCENTAGE_TENDERS_USING_EBID = "percentageTendersUsingEbid";
        public static final String PERCENTAGE_EGP = "percentEgp";
        public static final String TOTAL_TENDERS_WITH_LINKED_PROCUREMENT_PLAN = "totalTendersWithLinkedProcurementPlan";
	}

	@ApiOperation("Returns the percent of tenders that were cancelled, grouped by year."
			+ " The year is taken from tender.tenderPeriod.startDate. The response also contains the"
			+ " total number of tenders and total number of cancelled tenders for each year.")
    @RequestMapping(value = "/api/percentTendersCancelled", 
    method = { RequestMethod.POST, RequestMethod.GET }, produces = "application/json")
    public List<DBObject> percentTendersCancelled(@ModelAttribute @Valid final DefaultFilterPagingRequest filter) {

        DBObject project1 = new BasicDBObject();
        project1.put(Keys.YEAR, new BasicDBObject("$year", "$tender.tenderPeriod.startDate"));
        project1.put("tender.status", 1);

        DBObject group = new BasicDBObject();
        group.put(Fields.UNDERSCORE_ID, "$year");
        group.put(Keys.TOTAL_TENDERS, new BasicDBObject("$sum", 1));
        group.put(Keys.TOTAL_CANCELLED, new BasicDBObject("$sum", new BasicDBObject("$cond",
                Arrays.asList(new BasicDBObject("$eq", Arrays.asList("$tender.status", "cancelled")), 1, 0))));

        DBObject project2 = new BasicDBObject();
        project2.put(Fields.UNDERSCORE_ID, 0);
        project2.put(Keys.YEAR, Fields.UNDERSCORE_ID_REF);
        project2.put(Keys.TOTAL_TENDERS, 1);
        project2.put(Keys.TOTAL_CANCELLED, 1);
        project2.put(Keys.PERCENT_CANCELLED, new BasicDBObject("$multiply",
                Arrays.asList(new BasicDBObject("$divide", Arrays.asList("$totalCancelled", "$totalTenders")), 100)));

        Aggregation agg = newAggregation(
                match(where("tender.tenderPeriod.startDate").exists(true)
                        .andOperator(getDefaultFilterCriteria(filter))),
                new CustomProjectionOperation(project1), new CustomGroupingOperation(group),
                new CustomProjectionOperation(project2),
                sort(Direction.ASC, Keys.YEAR), skip(filter.getSkip()), limit(filter.getPageSize())
        );

        AggregationResults<DBObject> results = mongoTemplate.aggregate(agg, "release", DBObject.class);
        List<DBObject> list = results.getMappedResults();
        return list;
    }
	
	@ApiOperation("Percentage of tenders with >1 tenderer/bidder): "
			+ "Count of tenders with numberOfTenderers >1 divided by total count of tenders."
			+ "This endpoint uses tender.tenderPeriod.startDate to calculate the tender year.")
	@RequestMapping(value = "/api/percentTendersWithTwoOrMoreTenderers", 
			method = { RequestMethod.POST, RequestMethod.GET }, produces = "application/json")
    public List<DBObject> percentTendersWithTwoOrMoreTenderers(@ModelAttribute 
    		@Valid final DefaultFilterPagingRequest filter) {

        DBObject project1 = new BasicDBObject();
        project1.put(Keys.YEAR, new BasicDBObject("$year", "$tender.tenderPeriod.startDate"));
        project1.put("tender.numberOfTenderers", 1);

        DBObject group = new BasicDBObject();
        group.put(Fields.UNDERSCORE_ID, "$year");
        group.put(Keys.TOTAL_TENDERS, new BasicDBObject("$sum", 1));
        group.put("totalTendersWithTwoOrMoreTenderers", new BasicDBObject("$sum", new BasicDBObject("$cond",
                Arrays.asList(new BasicDBObject("$gt", Arrays.asList("$tender.numberOfTenderers", 1)), 1, 0))));        

        DBObject project2 = new BasicDBObject();
        project2.put(Fields.UNDERSCORE_ID, 0);
        project2.put(Keys.YEAR, Fields.UNDERSCORE_ID_REF);
        project2.put(Keys.TOTAL_TENDERS, 1);
        project2.put(Keys.TOTAL_TENDERS_WITH_TWO_OR_MORE_TENDERERS, 1);
		project2.put(Keys.PERCENT_TENDERS, new BasicDBObject("$multiply", Arrays.asList(
				new BasicDBObject("$divide", Arrays.asList("$totalTendersWithTwoOrMoreTenderers", "$totalTenders")),
				100)));

        Aggregation agg = newAggregation(
                match(where("tender.tenderPeriod.startDate").exists(true)
                        .andOperator(getDefaultFilterCriteria(filter))),
                new CustomProjectionOperation(project1), new CustomGroupingOperation(group),
                new CustomProjectionOperation(project2),
                sort(Direction.ASC, Keys.YEAR), skip(filter.getSkip()), limit(filter.getPageSize())
        );

        AggregationResults<DBObject> results = mongoTemplate.aggregate(agg, "release", DBObject.class);
        List<DBObject> list = results.getMappedResults();
        return list;
    }
	
	@ApiOperation("Percent of awarded tenders with >1 tenderer/bidder"
			+ "Count of tenders with numberOfTenderers >1 divided by total count of tenders with numberOfTenderers >0"
			+ "This endpoint uses tender.tenderPeriod.startDate to calculate the tender year.")
    @RequestMapping(value = "/api/percentTendersAwardedWithTwoOrMoreTenderers", 
    		method = { RequestMethod.POST, RequestMethod.GET }, produces = "application/json")
    public List<DBObject> percentTendersAwarded(@ModelAttribute @Valid final DefaultFilterPagingRequest filter) {

        DBObject project1 = new BasicDBObject();
        project1.put(Keys.YEAR, new BasicDBObject("$year", "$tender.tenderPeriod.startDate"));
        project1.put("tender.numberOfTenderers", 1);

        DBObject group = new BasicDBObject();
        group.put(Fields.UNDERSCORE_ID, "$year");
        group.put(Keys.TOTAL_TENDERS_WITH_ONE_OR_MORE_TENDERERS, new BasicDBObject("$sum", new BasicDBObject("$cond",
                Arrays.asList(new BasicDBObject("$gt", Arrays.asList("$tender.numberOfTenderers", 0)), 1, 0))));        
        
        group.put(Keys.TOTAL_TENDERS_WITH_TWO_OR_MORE_TENDERERS, new BasicDBObject("$sum", new BasicDBObject("$cond",
                Arrays.asList(new BasicDBObject("$gt", Arrays.asList("$tender.numberOfTenderers", 1)), 1, 0))));        

        DBObject project2 = new BasicDBObject();
        project2.put(Fields.UNDERSCORE_ID, 0);
        project2.put(Keys.YEAR, Fields.UNDERSCORE_ID_REF);
        project2.put(Keys.TOTAL_TENDERS_WITH_ONE_OR_MORE_TENDERERS, 1);
        project2.put(Keys.TOTAL_TENDERS_WITH_TWO_OR_MORE_TENDERERS, 1);
		project2.put(Keys.PERCENT_TENDERS,
				new BasicDBObject("$multiply", Arrays.asList(new BasicDBObject("$divide",
						Arrays.asList("$totalTendersWithTwoOrMoreTenderers", "$totalTendersWithOneOrMoreTenderers")),
						100)));

        Aggregation agg = newAggregation(
                match(where("tender.tenderPeriod.startDate").exists(true).and("tender.numberOfTenderers").gt(0)
                        .andOperator(getDefaultFilterCriteria(filter))),
                new CustomProjectionOperation(project1), new CustomGroupingOperation(group),
                new CustomProjectionOperation(project2),
                sort(Direction.ASC, Keys.YEAR), skip(filter.getSkip()), limit(filter.getPageSize())
        );

        AggregationResults<DBObject> results = mongoTemplate.aggregate(agg, "release", DBObject.class);
        List<DBObject> list = results.getMappedResults();
        return list;
    }
	
	
	
	@ApiOperation("Returns the percent of tenders with active awards, "
			+ "with tender.submissionMethod='electronicSubmission'."
			+ "The endpoint also returns the total tenderds with active awards and the count of tenders with "
			+ "tender.submissionMethod='electronicSubmission")
    @RequestMapping(value = "/api/percentTendersUsingEBid", method = { RequestMethod.POST, RequestMethod.GET },
    produces = "application/json")
    public List<DBObject> percentTendersUsingEBid(@ModelAttribute @Valid final DefaultFilterPagingRequest filter) {

        DBObject project1 = new BasicDBObject();
        project1.put("year", new BasicDBObject("$year", "$tender.tenderPeriod.startDate"));
        project1.put(Fields.UNDERSCORE_ID, "$tender._id");
		project1.put("electronicSubmission", new BasicDBObject("$cond", Arrays.asList(new BasicDBObject("$eq",
				Arrays.asList("$tender.submissionMethod", Tender.SubmissionMethod.electronicSubmission.toString())), 1,
				0)));

		DBObject group1 = new BasicDBObject();
		group1.put(Fields.UNDERSCORE_ID, Fields.UNDERSCORE_ID_REF);
		group1.put(Keys.YEAR, new BasicDBObject("$first", "$year"));
		group1.put("electronicSubmission", new BasicDBObject("$max", "$electronicSubmission"));
		
		DBObject group2 = new BasicDBObject();
		group2.put(Fields.UNDERSCORE_ID, "$year");
		group2.put(Keys.TOTAL_TENDERS, new BasicDBObject("$sum", 1));
		group2.put(Keys.TOTAL_TENDERS_USING_EBID, new BasicDBObject("$sum", "$electronicSubmission"));
               
		DBObject project2 = new BasicDBObject();
		project2.put(Fields.UNDERSCORE_ID, 0);
		project2.put(Keys.YEAR, Fields.UNDERSCORE_ID_REF);
		project2.put(Keys.TOTAL_TENDERS, 1);
		project2.put(Keys.TOTAL_TENDERS_USING_EBID, 1);
		project2.put(Keys.PERCENTAGE_TENDERS_USING_EBID, new BasicDBObject("$multiply", Arrays
				.asList(new BasicDBObject("$divide", Arrays.asList("$totalTendersUsingEbid", "$totalTenders")), 100)));

        Aggregation agg = newAggregation(
                match(where("tender.tenderPeriod.startDate").exists(true).and("tender.submissionMethod.0").exists(true).
                		and("awards.status").is("active")
                        .andOperator(getDefaultFilterCriteria(filter))),
                unwind("$tender.submissionMethod"),
                new CustomProjectionOperation(project1), new CustomGroupingOperation(group1),
                new CustomGroupingOperation(group2),
                new CustomProjectionOperation(project2),
                sort(Direction.ASC,  Keys.YEAR), skip(filter.getSkip()), limit(filter.getPageSize())
        );

        AggregationResults<DBObject> results = mongoTemplate.aggregate(agg, "release", DBObject.class);
        List<DBObject> list = results.getMappedResults();
        return list;
    }
	
	
	@ApiOperation("Returns the percent of tenders that are using eProcurement."
			+ " This is read from tender.publicationMethod='eGP'")
	@RequestMapping(value = "/api/percentTendersUsingEgp",  
	method = { RequestMethod.POST, RequestMethod.GET }, produces = "application/json")
	public List<DBObject> percentTendersUsingEgp(@ModelAttribute @Valid final DefaultFilterPagingRequest filter) {

		DBObject project1 = new BasicDBObject();
		project1.put("year", new BasicDBObject("$year", "$tender.tenderPeriod.startDate"));
		project1.put("tender.publicationMethod", 1);

		DBObject group = new BasicDBObject();
		group.put(Fields.UNDERSCORE_ID, "$year");
		group.put("totalTenders", new BasicDBObject("$sum", 1));
		group.put("totalEgp", new BasicDBObject("$sum", new BasicDBObject("$cond",
				Arrays.asList(new BasicDBObject("$eq", Arrays.asList("$tender.publicationMethod", "eGP")), 1, 0))));

		DBObject project2 = new BasicDBObject();
		project2.put(Fields.UNDERSCORE_ID, 0);
		project2.put("year", Fields.UNDERSCORE_ID_REF);
		project2.put("totalTenders", 1);
		project2.put("totalEgp", 1);
		project2.put("percentEgp", new BasicDBObject("$multiply",
				Arrays.asList(new BasicDBObject("$divide", Arrays.asList("$totalEgp", "$totalTenders")), 100)));

		Aggregation agg = newAggregation(
				match(where("tender.tenderPeriod.startDate").exists(true)
						.andOperator(getDefaultFilterCriteria(filter))),
				new CustomProjectionOperation(project1), new CustomGroupingOperation(group),
				new CustomProjectionOperation(project2), sort(Direction.ASC, "year"), skip(filter.getSkip()),
				limit(filter.getPageSize()));

		AggregationResults<DBObject> results = mongoTemplate.aggregate(agg, "release", DBObject.class);
		List<DBObject> list = results.getMappedResults();
		return list;
    }
	
	
	@ApiOperation("Percentage of tenders that are associated in releases that "
			+ "have the planning.budget.amount non empty,"
			+ "meaning there really is a planning entity correlated with the tender entity."
			+ "This endpoint uses tender.tenderPeriod.startDate to calculate the tender year.")
	@RequestMapping(value = "/api/percentTendersWithLinkedProcurementPlan", 
			method = { RequestMethod.POST, RequestMethod.GET }, produces = "application/json")
    public List<DBObject> percentTendersWithLinkedProcurementPlan(@ModelAttribute 
    		@Valid final DefaultFilterPagingRequest filter) {

        DBObject project1 = new BasicDBObject();
        project1.put(Keys.YEAR, new BasicDBObject("$year", "$tender.tenderPeriod.startDate"));
        project1.put("tender._id", 1);
        project1.put("planning.budget.amount", 1);

        DBObject group = new BasicDBObject();
        group.put(Fields.UNDERSCORE_ID, "$year");
        group.put(Keys.TOTAL_TENDERS, new BasicDBObject("$sum", 1));
		group.put(Keys.TOTAL_TENDERS_WITH_LINKED_PROCUREMENT_PLAN, new BasicDBObject("$sum", new BasicDBObject("$cond",
				Arrays.asList(new BasicDBObject("$gt", Arrays.asList("$planning.budget.amount", null)), 1, 0))));

        DBObject project2 = new BasicDBObject();
        project2.put(Fields.UNDERSCORE_ID, 0);
        project2.put(Keys.YEAR, Fields.UNDERSCORE_ID_REF);
        project2.put(Keys.TOTAL_TENDERS, 1);
        project2.put(Keys.TOTAL_TENDERS_WITH_LINKED_PROCUREMENT_PLAN, 1);
		project2.put(Keys.PERCENT_TENDERS,
				new BasicDBObject("$multiply",
						Arrays.asList(new BasicDBObject("$divide", Arrays.asList(
								"$" + Keys.TOTAL_TENDERS_WITH_LINKED_PROCUREMENT_PLAN, "$" + Keys.TOTAL_TENDERS)),
								100)));

        Aggregation agg = newAggregation(
                match(where("tender.tenderPeriod.startDate").exists(true)
                        .andOperator(getDefaultFilterCriteria(filter))),
                new CustomProjectionOperation(project1), new CustomGroupingOperation(group),
                new CustomProjectionOperation(project2),
                sort(Direction.ASC, Keys.YEAR), skip(filter.getSkip()), limit(filter.getPageSize())
        );

        AggregationResults<DBObject> results = mongoTemplate.aggregate(agg, "release", DBObject.class);
        List<DBObject> list = results.getMappedResults();
        return list;
    }
	
	
	@ApiOperation("")
	@RequestMapping(value = "/api/avgTimeFromPlanToTenderPhase", 
			method = { RequestMethod.POST, RequestMethod.GET }, produces = "application/json")
    public List<DBObject> avgTimeFromPlanToTenderPhase(@ModelAttribute 
    		@Valid final DefaultFilterPagingRequest filter) {

        DBObject project1 = new BasicDBObject();
        project1.put(Keys.YEAR, new BasicDBObject("$year", "$tender.tenderPeriod.startDate"));
        project1.put("tender._id", 1);
        project1.put("planning.budget.amount", 1);

        DBObject group = new BasicDBObject();
        group.put(Fields.UNDERSCORE_ID, "$year");
        group.put(Keys.TOTAL_TENDERS, new BasicDBObject("$sum", 1));
		group.put(Keys.TOTAL_TENDERS_WITH_LINKED_PROCUREMENT_PLAN, new BasicDBObject("$sum", new BasicDBObject("$cond",
				Arrays.asList(new BasicDBObject("$gt", Arrays.asList("$planning.budget.amount", null)), 1, 0))));

        DBObject project2 = new BasicDBObject();
        project2.put(Fields.UNDERSCORE_ID, 0);
        project2.put(Keys.YEAR, Fields.UNDERSCORE_ID_REF);
        project2.put(Keys.TOTAL_TENDERS, 1);
        project2.put(Keys.TOTAL_TENDERS_WITH_LINKED_PROCUREMENT_PLAN, 1);
		project2.put(Keys.PERCENT_TENDERS,
				new BasicDBObject("$multiply",
						Arrays.asList(new BasicDBObject("$divide", Arrays.asList(
								"$" + Keys.TOTAL_TENDERS_WITH_LINKED_PROCUREMENT_PLAN, "$" + Keys.TOTAL_TENDERS)),
								100)));

        Aggregation agg = newAggregation(
                match(where("tender.tenderPeriod.startDate").exists(true).
                		and("planning.budget.amount").exists(true)
                        .andOperator(getDefaultFilterCriteria(filter))),
                new CustomProjectionOperation(project1), new CustomGroupingOperation(group),
                new CustomProjectionOperation(project2),
                sort(Direction.ASC, Keys.YEAR), skip(filter.getSkip()), limit(filter.getPageSize())
        );

        AggregationResults<DBObject> results = mongoTemplate.aggregate(agg, "release", DBObject.class);
        List<DBObject> list = results.getMappedResults();
        return list;
    }

}