package org.devgateway.toolkit.persistence.hibernate;

import org.hibernate.internal.util.StringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.orm.jpa.hibernate.SpringNamingStrategy;
import org.springframework.util.Assert;

import javax.persistence.Embeddable;

/**
 * <p>
 * An extension to {@link SpringNamingStrategy} that allows to repeat
 * {@link Embeddable} entities of same type in one entity.
 * </p>
 *
 * Hibernate naming strategy used by default by Spring Boot does not allow to
 * repeat an @Embedded field with same type. For example this case will fail:
 *
 * <pre>
 * {
 *     &#64;code
 *
 *     &#64;literal
 *     &#64;Entity
 *     public class Employee {
 *
 *         &#64;literal
 *         &#64;Embedded
 *         private EmploymentPeriod spring;
 *
 *         &#64;literal
 *         &#64;Embedded
 *         private EmploymentPeriod fall;
 *     }
 *
 *     &#64;literal
 *     &#64;Embeddable
 *     public class EmploymentPeriod {
 *
 *         private java.sql.Date startDate;
 *
 *         private java.sql.Date endDate;
 *     }
 *
 * }
 * </pre>
 *
 * <p>
 * Hibernate fails because it will try to use start_date and end_date columns
 * two times. New strategy avoids this by assigning qualified names to embedded
 * properties (spring_start_date vs fall_start_date).
 * </p>
 *
 * Created by octavian on 8/18/16.
 */
public class EmbeddedFriendlySpringNamingStrategy extends SpringNamingStrategy {

    /**
     * Prints the difference between SpringNamingStrategy and
     * EmbeddedFriendlySpringNamingStrategy only for the fields where a
     * difference exists. Useful to understand the impact on existing schema if
     * you were using SpringNamingStrategy and plan to migrate to
     * EmbeddedFriendlySpringNamingStrategy.
     */
    private static final boolean PRINT_CHANGES = false;

    /**
     * Logged used to print differences.
     */
    private static final Logger logger = LoggerFactory.getLogger(EmbeddedFriendlySpringNamingStrategy.class);

    /**
     * <p>
     * This method has the same logic as its super except that this one does not
     * call StringHelper.unqualify()
     * </p>
     * {@inheritDoc}
     */
    @Override
    public String propertyToColumnName(final String propertyName) {
        String columnName = addUnderscores(propertyName);
        if (PRINT_CHANGES) {
            String oldColumnName = super.propertyToColumnName(propertyName);
            if (!equals(columnName, oldColumnName)) {
                logger.info("propertyToColumnName " + oldColumnName + " -> " + columnName);
            }
        }
        return columnName;
    }

    /**
     * <p>
     * This method has the same logic as its super except that this one does not
     * call StringHelper.unqualify()
     * </p>
     * {@inheritDoc}
     */
    @Override
    public String logicalColumnName(final String columnName, final String propertyName) {
        String logicalColumnName = StringHelper.isNotEmpty(columnName) ? columnName : propertyName;
        if (PRINT_CHANGES) {
            String oldLogicalColumnName = super.logicalColumnName(columnName, propertyName);
            if (!equals(logicalColumnName, oldLogicalColumnName)) {
                logger.info("logicalColumnName " + oldLogicalColumnName + " -> " + logicalColumnName);
            }
        }
        return logicalColumnName;
    }

    /**
     * <p>
     * This method has the same logic as its super except that this one does not
     * call StringHelper.unqualify()
     * </p>
     * {@inheritDoc}
     */
    @Override
    public String foreignKeyColumnName(final String propertyName, final String propertyEntityName,
            final String propertyTableName, final String referencedColumnName) {
        String name = propertyTableName;
        if (propertyName != null) {
            name = propertyName;
        }
        Assert.state(org.springframework.util.StringUtils.hasLength(name), "Unable to generate foreignKeyColumnName");
        String fkColumnName = columnName(name) + "_" + referencedColumnName;
        if (PRINT_CHANGES) {
            String oldFkColumnName = super.foreignKeyColumnName(propertyName, propertyEntityName, propertyTableName,
                    referencedColumnName);
            if (!equals(fkColumnName, oldFkColumnName)) {
                logger.info("foreignKeyColumnName " + oldFkColumnName + " -> " + fkColumnName);
            }
        }
        return fkColumnName;
    }

    /**
     * Compares if two strings are equal. Also returns null in case both strings
     * are null.
     *
     * @param cs1
     *            the first string, may be null
     * @param cs2
     *            the second string, may be null
     * @return true if strings are equal
     */
    private static boolean equals(final String cs1, final String cs2) {
        if (cs1 == cs2) {
            return true;
        }
        if (cs1 == null || cs2 == null) {
            return false;
        }
        return cs1.equals(cs2);
    }
}
