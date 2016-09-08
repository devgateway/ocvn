/**
 *
 */
package org.devgateway.toolkit.persistence.mongo.aggregate;

import com.mongodb.BasicDBObject;

/**
 * @author mihai
 *
 */
public class CustomUnwindOperation extends CustomOperation {

	/**
	 * @param operation
	 */
	public CustomUnwindOperation(String field) {
		super(new BasicDBObject("$unwind", field));
	}

}
