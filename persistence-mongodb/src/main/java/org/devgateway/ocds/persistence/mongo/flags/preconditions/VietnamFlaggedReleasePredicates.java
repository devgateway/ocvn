/**
 * 
 */
package org.devgateway.ocds.persistence.mongo.flags.preconditions;

import org.devgateway.ocds.persistence.mongo.FlaggedRelease;
import org.devgateway.ocds.persistence.mongo.Tender;

/**
 * @author mpostelnicu
 *
 */
public final class VietnamFlaggedReleasePredicates {

    private VietnamFlaggedReleasePredicates() {

    }
    
    public static final NamedPredicate<FlaggedRelease> ELECTRONIC_SUBMISSION = new NamedPredicate<>(
            "Needs to have electronic submission tender submission method",
            p -> p.getTender() != null && p.getTender().getSubmissionMethod() != null
                    && p.getTender().getSubmissionMethod().contains(Tender.SubmissionMethod.electronicSubmission));
}
