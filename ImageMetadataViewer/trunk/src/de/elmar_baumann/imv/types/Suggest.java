package de.elmar_baumann.imv.types;

import java.util.Collection;

/**
 * Suggests text.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-07-12
 */
public interface Suggest {

    /**
    +     * Suggests text.
     *
     * @param  text text for searching suggestions
     * @return      text suggestions for that text
     */
    public Collection<String> suggest(String text);

    /**
     * Returns a description what this class does.
     *
     * @return description
     */
    public String getDescription();
}
