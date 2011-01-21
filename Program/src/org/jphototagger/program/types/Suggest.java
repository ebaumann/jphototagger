package org.jphototagger.program.types;

import java.util.Collection;

/**
 * Suggests text.
 *
 * @author Elmar Baumann
 */
public interface Suggest {

    /**
     * Suggests text.
     *
     * @param  text text for searching suggestions
     * @return      text suggestions for that text
     */
    Collection<String> suggest(String text);

    /**
     * Returns a description what this class does.
     *
     * @return description
     */
    String getDescription();
}
