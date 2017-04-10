package org.devgateway.ocds.web.flags.release;

import org.devgateway.ocds.persistence.mongo.Award;
import org.devgateway.ocds.persistence.mongo.Detail;
import org.devgateway.ocds.persistence.mongo.FlaggedRelease;
import org.devgateway.ocds.persistence.mongo.flags.AbstractFlaggedReleaseFlagProcessor;
import org.devgateway.ocds.persistence.mongo.flags.Flag;
import org.devgateway.ocds.persistence.mongo.flags.FlagType;
import org.devgateway.ocds.persistence.mongo.flags.preconditions.FlaggedReleasePredicates;
import org.devgateway.ocds.web.rest.controller.GenericOCDSController;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * @author mpostelnicu
 *         <p>
 *         i085 Bids are an exact percentage apart
 */
@Component
public class ReleaseFlagI085Processor extends AbstractFlaggedReleaseFlagProcessor {

    public static final BigDecimal MAX_ALLOWED_PERCENT_BID_AWARD_AMOUNT = new BigDecimal(0.25);


    @PostConstruct
    @Override
    protected void setPredicates() {
        preconditionsPredicates = Collections.synchronizedList(Arrays.asList(
                FlaggedReleasePredicates.ACTIVE_AWARD,
                FlaggedReleasePredicates.ELECTRONIC_SUBMISSION
        ));
    }


    @Override
    protected void setFlag(Flag flag, FlaggedRelease flaggable) {
        flaggable.getFlags().setI085(flag);
    }

    @Override
    protected Set<FlagType> flagTypes() {
        return new HashSet<FlagType>(Arrays.asList(FlagType.RIGGING));
    }

    @Override
    protected Boolean calculateFlag(FlaggedRelease flaggable, StringBuffer rationale) {

        //get smallest bid
        Optional<Detail> smallestBid = flaggable.getBids().getDetails().stream()
                .min((o1, o2) -> o1.getValue().getAmount().compareTo(o2.getValue().getAmount()));

        boolean result = false;

        for (Detail bid : flaggable.getBids().getDetails()) {
            for (Award award : flaggable.getAwards()) {
                if (!Award.Status.active.equals(award.getStatus())) {
                    continue;
                }
                BigDecimal dLeft = relativeDistanceLeft(bid.getValue().getAmount(), award.getValue().getAmount()).
                        multiply(GenericOCDSController.ONE_HUNDRED);

                BigDecimal dRight = relativeDistanceRight(bid.getValue().getAmount(), award.getValue().getAmount()).
                        multiply(GenericOCDSController.ONE_HUNDRED);


                rationale.append("Award=").append(award.getValue().getAmount())
                        .append(";bid=").append(bid.getValue().getAmount());

                if (BigDecimal.valueOf(dLeft.intValue()).equals(dLeft)
                        || BigDecimal.valueOf(dRight.intValue()).equals(dRight)) {
                    result = true;
                    break;
                }


            }
        }
        return result;
    }


}