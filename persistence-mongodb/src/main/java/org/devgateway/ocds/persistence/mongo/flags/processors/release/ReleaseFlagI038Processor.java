/**
 * 
 */
package org.devgateway.ocds.persistence.mongo.flags.processors.release;

import org.devgateway.ocds.persistence.mongo.FlaggedRelease;
import org.devgateway.ocds.persistence.mongo.flags.AbstractFlaggedReleaseFlagProcessor;
import org.devgateway.ocds.persistence.mongo.flags.Flag;
import org.devgateway.ocds.persistence.mongo.flags.preconditions.FlaggedReleasePredicates;
import org.devgateway.ocds.persistence.mongo.flags.preconditions.NamedPredicate;
import org.joda.time.DateTime;
import org.joda.time.Days;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * @author mpostelnicu
 *
 */
public class ReleaseFlagI038Processor extends AbstractFlaggedReleaseFlagProcessor {

    public static final int MIN_ALLOWED_DAYS_BIDDING_PERIOD = 7;

    public static final ReleaseFlagI038Processor INSTANCE = new ReleaseFlagI038Processor();

    @Override
    protected void setFlag(Flag flag, FlaggedRelease flaggable) {
        flaggable.getFlags().setI038(flag);
    }

    @Override
    protected Boolean calculateFlag(FlaggedRelease flaggable, StringBuffer rationale) {
        Days daysBetween = Days.daysBetween(new DateTime(flaggable.getTender().getTenderPeriod().getStartDate()),
                new DateTime(flaggable.getTender().getTenderPeriod().getEndDate()));
        rationale.append("Days between: ").append(daysBetween.getDays()).append("; Minimum allowed days: ")
                .append(MIN_ALLOWED_DAYS_BIDDING_PERIOD).append(";");
        return daysBetween.getDays() < MIN_ALLOWED_DAYS_BIDDING_PERIOD;

    }

    @Override
    protected Collection<NamedPredicate<FlaggedRelease>> getPreconditionsPredicates() {
        return Collections.unmodifiableList(Arrays.asList(FlaggedReleasePredicates.OPEN_PROCUREMENT_METHOD,
                FlaggedReleasePredicates.TENDER_END_DATE, FlaggedReleasePredicates.TENDER_START_DATE));
    }

}
