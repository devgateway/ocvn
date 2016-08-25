package org.devgateway.ocds.persistence.mongo.repository;

import org.devgateway.ocds.persistence.mongo.Release;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface ReleaseRepository extends MongoRepository<Release, String> {

    @Query(value = "{ 'planning.budget.projectID' : ?0 }")
    Release findByBudgetProjectId(String projectId);

    Release findByOcid(String ocid);

    Release findById(String id);
    
    /**
     * Vietnam specific planning bid no find
     * 
     * @param bidNo
     * @return
     */
    @Query(value = "{ 'planning.bidNo' : ?0 }")
    Release findByPlanningBidNo(String bidNo);

    @Override
    <S extends Release> S save(S entity);
}
