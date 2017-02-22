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

import io.swagger.annotations.ApiOperation;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.validation.Valid;
import org.devgateway.ocds.web.rest.controller.request.YearFilterPagingRequest;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author mpostelnicu
 */
@RestController
@CacheConfig(keyGenerator = "genericPagingRequestKeyGenerator", cacheNames = "genericPagingRequestJson")
@Cacheable
public class TotalFlagsController extends GenericOCDSController {

    public static class TypeValue {
        private String id;
        private Integer value;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public Integer getValue() {
            return value;
        }

        public void setValue(Integer value) {
            this.value = value;
        }
    }

    public static final class Keys {
        public static final String ALL = "all";
    }


    @ApiOperation(value = "Counts the indicators flagged as true, and groups them by indicator type. "
            + "An indicator that has two types it will be counted twice, once in each group.")
    @RequestMapping(value = "/api/totalFlagsByIndicatorType", method = {RequestMethod.POST, RequestMethod.GET},
            produces = "application/json")
    public List<TypeValue> totalFlagsByIndicatorType(@ModelAttribute @Valid final YearFilterPagingRequest filter) {

        return StreamSupport
                .stream(mongoTemplate.mapReduce(
                        new Query(getYearDefaultFilterCriteria(filter, "tender.tenderPeriod.startDate")),
                        "release", "classpath:total-flags-by-indicator-type-map.js",
                        "classpath:total-flags-by-indicator-type-reduce.js",
                        TypeValue.class).spliterator(), false)
                .collect(Collectors.toList());
    }

    @ApiOperation(value = "Counts the indicators flagged as true. An indicator that has two types will be counted"
            + "only once.")
    @RequestMapping(value = "/api/totalFlags", method = {RequestMethod.POST, RequestMethod.GET},
            produces = "application/json")
    public List<TypeValue> totalFlags(@ModelAttribute @Valid final YearFilterPagingRequest filter) {

        return StreamSupport
                .stream(mongoTemplate.mapReduce(
                        new Query(getYearDefaultFilterCriteria(filter, "tender.tenderPeriod.startDate")),
                        "release", "classpath:total-flags-map.js",
                        "classpath:total-flags-reduce.js",
                        TypeValue.class).spliterator(), false)
                .collect(Collectors.toList());
    }


}