/**
 *
 */
package org.devgateway.ocds.persistence.mongo.repository;

import java.util.Collection;
import java.util.List;

import org.devgateway.ocds.persistence.mongo.Organization;
import org.devgateway.ocds.persistence.mongo.Organization.OrganizationType;
import org.springframework.data.mongodb.repository.Query;

/**
 * @author mpostelnicu
 *
 */
public interface OrganizationRepository extends GenericOrganizationRepository<Organization> {

    @Query(value = "{ $or: [ {'_id' :  { $in : ?0 }}, " + "{'additionalIdentifiers.identifier._id': { $in : ?0 }} ] }")
    List<Organization> findByIdCollection(Collection<String> idCol);

    @Query(value = "{$and: [  { $or: [ {'_id' : ?0 }, "
            + "{'additionalIdentifiers.identifier._id': ?0} ] }, { 'types': ?1 } ] }")
    Organization findByAllIdsAndType(String id, OrganizationType type);
}
