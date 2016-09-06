/**
 * 
 */
package org.devgateway.ocds.persistence.mongo.repository;

import org.devgateway.ocds.persistence.mongo.Organization;
import org.devgateway.ocds.persistence.mongo.Organization.OrganizationType;
import org.springframework.data.mongodb.repository.Query;

/**
 * @author mihai
 *
 */
public interface OrganizationRepository extends GenericOrganizationRepository<Organization> {

	@Query(value = "{$and: [ { $or: [ {'_id' : ?0 }, "
			+ "{'name': {$regex : '^?0$', $options: 'i'} } ] }  , { 'types': ?1 } ]}")
	Organization findByIdOrNameInsensitiveAndTypes(String id, OrganizationType type);
	
	@Query(value = "{ $or: [ {'_id' : ?0 }, " + "{'name': {$regex : '^?0$', $options: 'i'} } ] }")
	Organization findByIdOrNameInsensitive(String idName);

	Organization findByIdAndTypes(String id, OrganizationType type);

}
