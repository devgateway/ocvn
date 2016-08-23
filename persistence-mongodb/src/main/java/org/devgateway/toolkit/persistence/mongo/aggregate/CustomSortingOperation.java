/**
 *
 */
package org.devgateway.toolkit.persistence.mongo.aggregate;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

/**
 * @author mihai
 *
 */
public class CustomSortingOperation extends CustomOperation {

    /**
     * @param operation
     */
    public CustomSortingOperation(final DBObject operation) {
        super(new BasicDBObject("$sort", operation));
    }

}
