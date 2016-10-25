/**
 *
 */
package org.devgateway.ocvn.persistence.mongo.dao;

import org.devgateway.ocds.persistence.mongo.excel.annotation.ExcelExport;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author mihai
 *
 */
@Document(collection = "city")
public class City {
    @Id
    private String id;

    @ExcelExport
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
