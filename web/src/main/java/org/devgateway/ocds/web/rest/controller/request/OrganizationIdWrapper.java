package org.devgateway.ocds.web.rest.controller.request;

import java.util.List;

import cz.jirutka.validator.collection.constraints.EachPattern;
import io.swagger.annotations.ApiModelProperty;

public class OrganizationIdWrapper {
    
    @EachPattern(regexp = "^[a-zA-Z0-9]*$")
    @ApiModelProperty(value = "List of organization identifiers")
    private List<String> id;

    public List<String> getId() {
        return id;
    }

    public void setId(List<String> ids) {
        this.id = ids;
    }

}
