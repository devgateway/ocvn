package org.devgateway.ocds.persistence.mongo.flags;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import org.devgateway.ocds.persistence.mongo.flags.preconditions.NamedPredicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractFlagProcessor<T extends Flaggable> {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * Calculates the given flag and returns the flag value. Does not set any
     * flag to the given {@link Flaggable}
     * 
     * @param release
     *            a {@link Flaggable}
     * @return the value of the flag. This can be null (not applicable), false ,
     *         or true
     */
    protected abstract Boolean calculateFlag(T flaggable, StringBuffer rationale);

    /**
     * Decides if the current {@link Flaggable} is eligible for this flag
     * 
     * @param flaggable
     * @return true if is eligible, false otherwise
     */
    protected abstract Collection<NamedPredicate<T>> getPreconditionsPredicates();

    /**
     * Processes the flag on the flaggable. only if the current
     * {@link Flaggable} {@link #checkPreconditions(Flaggable)}. invokes
     * {@link #calculateFlag(Flaggable)} then {@link #setFlag(Flag, Flaggable)}
     * with the {@link #generateRationale(Boolean, Flaggable)}
     * 
     * @param flaggable
     */
    public final void process(T flaggable) {
        Boolean flagValue = null;
        StringBuffer rationale = new StringBuffer();
        Set<NamedPredicate<T>> failedPreconditionsPredicates = getPreconditionsPredicates().parallelStream()
                .filter(predicate -> !predicate.test(flaggable)).collect(Collectors.toSet());

        if (failedPreconditionsPredicates.isEmpty()) {
            logger.debug("Flaggable " + flaggable.getIdProperty() + " does meet all preconditions. Calculating flag.");
            flagValue = calculateFlag(flaggable, rationale);
        } else {
            logger.debug("Flaggable " + flaggable.getIdProperty()
                    + " does NOT meet all preconditions. Dumping failed predicates.");
            rationale.append("Preconditions that are not met: ");
            failedPreconditionsPredicates.forEach(p -> rationale.append(p.toString()).append(" "));
        }

        logger.debug("Setting flag with value " + flagValue + " to flaggable " + flaggable.getIdProperty());
        setFlag(new Flag(flagValue, rationale.toString()), flaggable);
    }

    /**
     * Sets the flag to the {@link Flaggable}
     * 
     * @param flag
     *            the new flag
     * @param flaggable
     *            the flaggable to set the flag to
     */
    protected abstract void setFlag(Flag flag, T flaggable);

}
