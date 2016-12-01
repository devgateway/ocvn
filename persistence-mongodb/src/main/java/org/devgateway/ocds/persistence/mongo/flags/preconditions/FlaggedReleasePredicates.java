package org.devgateway.ocds.persistence.mongo.flags.preconditions;

import org.devgateway.ocds.persistence.mongo.Award;
import org.devgateway.ocds.persistence.mongo.FlaggedRelease;
import org.devgateway.ocds.persistence.mongo.Tender.ProcurementMethod;

/**
 * 
 * @author mpostelnicu
 *
 */
public final class FlaggedReleasePredicates {

    private FlaggedReleasePredicates() {

    }

    public static final NamedPredicate<FlaggedRelease> TENDER_START_DATE = new NamedPredicate<>(
            "Needs to have tender start date", p -> p.getTender() != null && p.getTender().getTenderPeriod() != null
                    && p.getTender().getTenderPeriod().getStartDate() != null);

    public static final NamedPredicate<FlaggedRelease> TENDER_END_DATE =
            new NamedPredicate<>("Needs to have tender end date", p -> p.getTender() != null
                    && p.getTender().getTenderPeriod() != null && p.getTender().getTenderPeriod().getEndDate() != null);

    public static final NamedPredicate<FlaggedRelease> OPEN_PROCUREMENT_METHOD =
            new NamedPredicate<>("Needs to have open tender procurement method",
                    p -> p.getTender() != null && ProcurementMethod.open.equals(p.getTender().getProcurementMethod()));

    public static final NamedPredicate<FlaggedRelease> ACTIVE_AWARD =
            new NamedPredicate<>("Needs to have at least one active award;",
                    p -> p.getAwards().stream().filter(a -> Award.Status.active.equals(a.getStatus())).count() > 0);

    public static final NamedPredicate<FlaggedRelease> UNSUCCESSFUL_AWARD = new NamedPredicate<>(
            "Needs to have at least one unsuccessful award",
            p -> p.getAwards().stream().filter(a -> Award.Status.unsuccessful.equals(a.getStatus())).count() > 0);

}
