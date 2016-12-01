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

    public static final NamedPredicate<FlaggedRelease> ELECTRONIC_SUBMISSION =
            new NamedPredicate<>("ELECTRONIC_SUBMISSION", p -> p.getTender() != null
                    && Tender.SubmissionMethod.electronicSubmission.equals(p.getTender().getSubmissionMethod()));

}
