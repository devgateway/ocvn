package org.devgateway.ocds.persistence.mongo.flags.processors.release.vietnam;

import org.devgateway.ocds.persistence.mongo.Award;
import org.devgateway.ocds.persistence.mongo.FlaggedRelease;
import org.devgateway.ocds.persistence.mongo.flags.AbstractFlaggedReleaseFlagProcessor;
import org.devgateway.ocds.persistence.mongo.flags.Flag;
import org.devgateway.ocds.persistence.mongo.flags.preconditions.FlaggedReleasePredicates;
import org.devgateway.ocvn.persistence.mongo.dao.VNAward;

import java.util.Arrays;
import java.util.Collections;

/**
 * @author mpostelnicu
 * 
 * i003 Only winning bidder was eligible for a tender that had multiple bidders.
 */
public class ReleaseFlagI003Processor extends AbstractFlaggedReleaseFlagProcessor {

    public static final ReleaseFlagI003Processor INSTANCE = new ReleaseFlagI003Processor();

    public ReleaseFlagI003Processor() {
        preconditionsPredicates = Collections.unmodifiableList(Arrays.asList(
                FlaggedReleasePredicates.ACTIVE_AWARD,
                FlaggedReleasePredicates.UNSUCCESSFUL_AWARD,
                FlaggedReleasePredicates.OPEN_PROCUREMENT_METHOD,
                FlaggedReleasePredicates.ELECTRONIC_SUBMISSION
        ));
    }

    @Override
    protected void setFlag(Flag flag, FlaggedRelease flaggable) {
        flaggable.getFlags().setI003(flag);
    }

    @Override
    protected Boolean calculateFlag(FlaggedRelease flaggable, StringBuffer rationale) {
        long eligibleUnsuccessfulAwards = flaggable.getAwards().stream().map(a -> (VNAward) a)
                .filter(a -> Award.Status.unsuccessful.equals(a.getStatus()) && !"Y".equals(a.getIneligibleYN()))
                .count();
        rationale.append("Number of eligible unsuccessful awards: ").append(eligibleUnsuccessfulAwards);
        return eligibleUnsuccessfulAwards == 0;
    }

}