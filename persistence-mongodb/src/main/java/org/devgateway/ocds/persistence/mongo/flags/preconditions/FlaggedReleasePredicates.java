package org.devgateway.ocds.persistence.mongo.flags.preconditions;

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

    public static final NamedPredicate<FlaggedRelease> HAS_TENDER_START_DATE = new NamedPredicate<>(
            "HAS_TENDER_START_DATE", p -> p.getTender() != null && p.getTender().getTenderPeriod() != null
                    && p.getTender().getTenderPeriod().getStartDate() != null);

    public static final NamedPredicate<FlaggedRelease> HAS_TENDER_END_DATE =
            new NamedPredicate<>("HAS_TENDER_END_DATE", p -> p.getTender() != null
                    && p.getTender().getTenderPeriod() != null && p.getTender().getTenderPeriod().getEndDate() != null);

    public static final NamedPredicate<FlaggedRelease> HAS_OPEN_PROCUREMENT_METHOD =
            new NamedPredicate<>("HAS_OPEN_PROCUREMENT_METHOD",
                    p -> p.getTender() != null && ProcurementMethod.open.equals(p.getTender().getProcurementMethod()));

}
