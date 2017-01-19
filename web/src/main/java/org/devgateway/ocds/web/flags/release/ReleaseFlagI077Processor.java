package org.devgateway.ocds.web.flags.release;


import org.devgateway.ocds.persistence.mongo.FlaggedRelease;
import org.devgateway.ocds.persistence.mongo.flags.AbstractFlaggedReleaseFlagProcessor;
import org.devgateway.ocds.persistence.mongo.flags.Flag;
import org.devgateway.ocds.persistence.mongo.flags.preconditions.FlaggedReleasePredicates;
import org.devgateway.ocds.web.rest.controller.FrequentSuppliersTimeIntervalController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author mpostelnicu
 *         <p>
 *         i077 High number of contract awards to one supplier within a given time period by a single procurement entity
 */
@Component
public class ReleaseFlagI077Processor extends AbstractFlaggedReleaseFlagProcessor {

    public static final Integer INTERVAL_DAYS = 365;
    public static final Integer MAX_AWARDS = 3;

    private ConcurrentHashMap<String, FrequentSuppliersTimeIntervalController.FrequentSuppliersTuple>
            awardsMap;

    @Autowired
    private FrequentSuppliersTimeIntervalController frequentSuppliersTimeIntervalController;

    @Override
    protected void setFlag(Flag flag, FlaggedRelease flaggable) {
        flaggable.getFlags().setI077(flag);
    }

    @Override
    protected Boolean calculateFlag(FlaggedRelease flaggable, StringBuffer rationale) {
        return flaggable.getAwards().stream().filter(award ->
                awardsMap.get(award.getId()) != null).map(award -> rationale
                .append("Award " + award.getId() + " flagged by tuple " + awardsMap.get(award.getId()) + "; "))
                .count() > 0;
    }

    @PostConstruct
    @Override
    protected void setPredicates() {
        preconditionsPredicates = Collections.synchronizedList(
                Arrays.asList(FlaggedReleasePredicates.ACTIVE_AWARD_WITH_DATE,
                        FlaggedReleasePredicates.TENDER_PROCURING_ENTITY));

        List<FrequentSuppliersTimeIntervalController.FrequentSuppliersTuple> frequentSuppliersTimeInterval
                = frequentSuppliersTimeIntervalController.frequentSuppliersTimeInterval(INTERVAL_DAYS, MAX_AWARDS);

        awardsMap = new ConcurrentHashMap<>();

        frequentSuppliersTimeInterval.
                forEach(tuple -> tuple.getAwardIds().forEach(awardId -> awardsMap.put(awardId, tuple)));
    }

}