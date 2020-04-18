package org.jphototagger.api.file;

import java.util.Collection;

/**
 * Stragies how to create a subdirectory defined by the user.
 *
 * @author Elmar Baumann
 */
public interface UserDefinedSubdirectoryCreateStrategies {

    /**
     * @return Zero or more user defined strategies
     */
    Collection<SubdirectoryCreateStrategy> getStrageties();

    /**
     * Offers the user editing her/his strategies.
     *
     * @return true, if at least one stratgey has been modified (added, edited,
     *         deleted)
     */
    boolean edit();
}
