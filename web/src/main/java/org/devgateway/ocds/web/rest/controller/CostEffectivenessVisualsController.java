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
import static org.springframework.data.mongodb.core.aggregation.Aggregation.skip;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.unwind;
import static org.springframework.data.mongodb.core.query.Criteria.where;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.devgateway.ocds.persistence.mongo.Award;
import org.devgateway.ocds.persistence.mongo.Tender;
import org.devgateway.ocds.web.rest.controller.request.DefaultFilterPagingRequest;
import org.devgateway.ocds.web.rest.controller.request.GroupingFilterPagingRequest;
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
public class CostEffectivenessVisualsController extends GenericOCDSController {


    public static final class Keys {
        public static final String TOTAL_AWARD_AMOUNT = "totalAwardAmount";
        public static final String TOTAL_AWARDS = "totalAwards";
        public static final String TOTAL_AWARDS_WITH_TENDER = "totalAwardsWithTender";
        public static final String PERCENTAGE_AWARDS_WITH_TENDER = "percentageAwardsWithTender";
        public static final String TOTAL_TENDER_AMOUNT = "totalTenderAmount";
        public static final String TOTAL_TENDERS = "totalTenders";
        public static final String TOTAL_TENDER_WITH_AWARDS = "totalTenderWithAwards";
        public static final String PERCENTAGE_TENDERS_WITH_AWARDS = "percentageTendersWithAwards";
        public static final String DIFF_TENDER_AWARD_AMOUNT = "diffTenderAwardAmount";
    }


    @ApiOperation(value = "Cost effectiveness of Awards: Displays the total amount of active awards grouped by year."
            + "The tender entity, for each award, has to have amount value. The year is calculated from awards.date")
    @RequestMapping(value = "/api/costEffectivenessAwardAmount",
            method = { RequestMethod.POST, RequestMethod.GET }, produces = "application/json")
    public List<DBObject> costEffectivenessAwardAmount(
            @ModelAttribute @Valid final DefaultFilterPagingRequest filter) {

        DBObject project = new BasicDBObject();
        project.put("year", new BasicDBObject("$year", "$awards.date"));
        project.put("awards.value.amount", 1);
        project.put("totalAwardsWithTender", new BasicDBObject("$cond",
                Arrays.asList(new BasicDBObject("$gt", Arrays.asList("$tender.tenderPeriod.startDate", null)), 1, 0)));
        project.put("awardsWithTenderValue",
                new BasicDBObject("$cond",
                        Arrays.asList(new BasicDBObject("$gt", Arrays.asList("$tender.tenderPeriod.startDate", null)),
                                "$awards.value.amount", 0)));

        DBObject project1 = new BasicDBObject();
        project1.put(Fields.UNDERSCORE_ID, 1);
        project1.put(Keys.TOTAL_AWARD_AMOUNT, 1);
        project1.put(Keys.TOTAL_AWARDS, 1);
        project1.put(Keys.TOTAL_AWARDS_WITH_TENDER, 1);
        project1.put(Keys.PERCENTAGE_AWARDS_WITH_TENDER, new BasicDBObject("$multiply", Arrays
                .asList(new BasicDBObject("$divide", Arrays.asList("$totalAwardsWithTender", "$totalAwards")), 100)));

        Aggregation agg = Aggregation.newAggregation(
                match(where("awards").elemMatch(where("status").is(Award.Status.active.toString())).and("awards.date")
                        .exists(true)),
                getMatchDefaultFilterOperation(filter), unwind("$awards"),
                match(where("awards.status").is(Award.Status.active.toString()).and("awards.value").exists(true)),
                new CustomProjectionOperation(project),
                group("$year").sum("awardsWithTenderValue").as(Keys.TOTAL_AWARD_AMOUNT).count().as(Keys.TOTAL_AWARDS)
                        .sum("totalAwardsWithTender").as(Keys.TOTAL_AWARDS_WITH_TENDER),
                new CustomProjectionOperation(project1), sort(Direction.ASC, Fields.UNDERSCORE_ID),
                skip(filter.getSkip()), limit(filter.getPageSize()));

        AggregationResults<DBObject> results = mongoTemplate.aggregate(agg, "release", DBObject.class);
        List<DBObject> tagCount = results.getMappedResults();
        return tagCount;
    }

    @ApiOperation(value = "Cost effectiveness of Tenders:"
            + " Displays the total amount of the active tenders that have active awards, "
            + "grouped by year. Only tenders.status=active"
            + "are taken into account. The year is calculated from tenderPeriod.startDate")
    @RequestMapping(value = "/api/costEffectivenessTenderAmount",
            method = { RequestMethod.POST, RequestMethod.GET }, produces = "application/json")
    public List<DBObject> costEffectivenessTenderAmount(
            @ModelAttribute @Valid final GroupingFilterPagingRequest filter) {

        DBObject project = new BasicDBObject();
        project.put("year", new BasicDBObject("$year", "$tender.tenderPeriod.startDate"));
        project.put("tender.value.amount", 1);
        project.put(Fields.UNDERSCORE_ID, "$tender._id");
        project.put("tenderWithAwards",
                new BasicDBObject("$cond", Arrays.asList(
                        new BasicDBObject("$eq", Arrays.asList("$awards.status", Award.Status.active.toString())), 1,
                        0)));
        project.put("tenderWithAwardsValue", new BasicDBObject("$cond",
                Arrays.asList(new BasicDBObject("$eq", Arrays.asList("$awards.status", Award.Status.active.toString())),
                        "$tender.value.amount", 0)));
        project.putAll(filterProjectMap);

        DBObject group1 = new BasicDBObject();
        group1.put(Fields.UNDERSCORE_ID, Fields.UNDERSCORE_ID_REF);
        group1.put("year", new BasicDBObject("$first", "$year"));
        group1.put("tenderWithAwards", new BasicDBObject("$max", "$tenderWithAwards"));
        group1.put("tenderWithAwardsValue", new BasicDBObject("$max", "$tenderWithAwardsValue"));
        group1.put("tenderAmount", new BasicDBObject("$first", "$tender.value.amount"));
        filterProjectMap.forEach((k, v) -> group1.put(k.replace(".", ""), new BasicDBObject("$first", "$" + k)));

        DBObject project2 = new BasicDBObject();
        project2.put(Fields.UNDERSCORE_ID, Fields.UNDERSCORE_ID_REF);
        project2.put(Keys.TOTAL_TENDER_AMOUNT, 1);
        project2.put(Keys.TOTAL_TENDERS, 1);
        project2.put(Keys.TOTAL_TENDER_WITH_AWARDS, 1);
        project2.put(Keys.PERCENTAGE_TENDERS_WITH_AWARDS, new BasicDBObject("$multiply", Arrays
                .asList(new BasicDBObject("$divide", Arrays.asList("$totalTenderWithAwards", "$totalTenders")), 100)));

        Aggregation agg = Aggregation.newAggregation(
                match(where("tender.status").is(Tender.Status.active.toString()).and("tender.tenderPeriod.startDate")
                        .exists(true)),
                getMatchDefaultFilterOperation(filter), unwind("$awards"), new CustomProjectionOperation(project),
                new CustomGroupingOperation(group1),
                getTopXFilterOperation(filter, "$year").sum("tenderWithAwardsValue").as(Keys.TOTAL_TENDER_AMOUNT)
                        .count()
                        .as(Keys.TOTAL_TENDERS).sum("tenderWithAwards").as(Keys.TOTAL_TENDER_WITH_AWARDS),
                new CustomProjectionOperation(project2), sort(Direction.ASC, Fields.UNDERSCORE_ID),
                skip(filter.getSkip()), limit(filter.getPageSize()));

        AggregationResults<DBObject> results = mongoTemplate.aggregate(agg, "release", DBObject.class);
        List<DBObject> tagCount = results.getMappedResults();

        return tagCount;

    }


    @ApiOperation(value = "Aggregated version of /api/costEffectivenessTenderAmount and "
            + "/api/costEffectivenessAwardAmount."
            + "This endpoint aggregates the responses from the specified endpoints, per year. "
            + "Responds to the same filters.")
    @RequestMapping(value = "/api/costEffectivenessTenderAwardAmount", method = { RequestMethod.POST,
            RequestMethod.GET }, produces = "application/json")
    public List<DBObject> costEffectivenessTenderAwardAmount(
            @ModelAttribute @Valid final GroupingFilterPagingRequest filter) {

        List<DBObject> costEffectivenessAwardAmount = costEffectivenessAwardAmount(filter);
        List<DBObject> costEffectivenessTenderAmount = costEffectivenessTenderAmount(filter);

        LinkedHashMap<Integer, DBObject> response = new LinkedHashMap<>();

        costEffectivenessAwardAmount.forEach(dbobj -> response.put((Integer) dbobj.get(Fields.UNDERSCORE_ID), dbobj));
        costEffectivenessTenderAmount.forEach(dbobj -> {
                    if (response.containsKey(dbobj.get(Fields.UNDERSCORE_ID))) {
                        Map<?, ?> map = dbobj.toMap();
                        map.remove(Fields.UNDERSCORE_ID);
                        response.get(dbobj.get(Fields.UNDERSCORE_ID)).putAll(map);
                    } else {
                        response.put((Integer) dbobj.get(Fields.UNDERSCORE_ID), dbobj);
                    }
                }
        );

        Collection<DBObject> respCollection = response.values();
        respCollection.forEach(dbobj -> {
            dbobj.put(Keys.DIFF_TENDER_AWARD_AMOUNT,
                    BigDecimal
                            .valueOf(dbobj.get(Keys.TOTAL_TENDER_AMOUNT) == null ? 0d
                                    : ((Number) dbobj.get(Keys.TOTAL_TENDER_AMOUNT)).doubleValue())
                            .subtract(BigDecimal.valueOf(dbobj.get(Keys.TOTAL_AWARD_AMOUNT) == null ? 0d
                                    : ((Number) dbobj.get(Keys.TOTAL_AWARD_AMOUNT)).doubleValue())));
        });

        return new ArrayList<>(respCollection);
    }



}