package org.devgateway.ocds.web.rest.controller.selector;

import java.util.List;

import javax.validation.Valid;

import org.devgateway.ocds.persistence.mongo.Organization;
import org.devgateway.ocds.web.rest.controller.request.OrganizationSearchRequest;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;

/**
 * 
 * @author mpostelnicu
 * 
 */
@RestController
public class OrganizationSearchController extends AbstractOrganizationSearchController {

	/**
	 * Searches organizations based on ID. Returns only one result, if the id
	 * exactly matches
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/api/ocds/organization/id/{id:^[a-zA-Z0-9]*$}", method = { RequestMethod.POST,
			RequestMethod.GET }, produces = "application/json")
	@ApiOperation(value = "Finds organization entity by the given id")
	public Organization byId(@PathVariable final String id) {
		return organizationRepository.findOne(id);
	}

	@RequestMapping(value = "/api/ocds/organization/all", method = { RequestMethod.POST,
			RequestMethod.GET }, produces = "application/json")
	@ApiOperation(value = "Lists all organizations in the database. "
			+ "Allows full text search using the text parameter.")
	public List<Organization> searchText(@Valid final OrganizationSearchRequest request) {
		return organizationSearchTextByType(request, null);
	}

}