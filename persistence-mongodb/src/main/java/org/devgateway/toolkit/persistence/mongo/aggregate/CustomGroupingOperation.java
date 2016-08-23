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
public class CustomGroupingOperation extends CustomOperation {

    /**
     * @param operation
     */
    public CustomGroupingOperation(final DBObject operation) {
        super(new BasicDBObject("$group", operation));
    }

}
