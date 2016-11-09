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
package org.devgateway.ocds.web.rest.controller.selector;

import java.util.List;

import javax.validation.Valid;

import org.devgateway.ocds.web.rest.controller.GenericOCDSController;
import org.devgateway.ocds.web.rest.controller.request.TextSearchRequest;
import org.devgateway.ocvn.persistence.mongo.dao.VNLocation;
import org.devgateway.ocvn.persistence.mongo.repository.VNLocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.mongodb.core.query.TextQuery;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 
 * @author mpostelnicu
 * 
 */
@RestController
public class LocationSearchController extends GenericOCDSController {

    @Autowired
    private VNLocationRepository locationRepository;

    @RequestMapping(value = "/api/ocds/location/all", method = { RequestMethod.POST, RequestMethod.GET },
            produces = "application/json")
    public List<VNLocation> locationsAll() {

        return locationRepository.findAll(new Sort(Direction.ASC, Fields.UNDERSCORE_ID));

    }

    @RequestMapping(value = "/api/ocds/location/search", method = { RequestMethod.POST, RequestMethod.GET },
            produces = "application/json")
    public List<VNLocation> locationsSearch(@ModelAttribute @Valid final TextSearchRequest request) {
        PageRequest pageRequest = new PageRequest(request.getPageNumber(), request.getPageSize());

        Query query = null;

        if (request.getText() == null) {
            query = new Query();
        } else {
            query = TextQuery.queryText(new TextCriteria().matching(request.getText())).sortByScore();
        }

        query.with(pageRequest);

        return mongoTemplate.find(query, VNLocation.class);

    }

}