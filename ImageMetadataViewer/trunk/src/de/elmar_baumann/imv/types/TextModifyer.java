package de.elmar_baumann.imv.types;

import java.util.Collection;

/**
 * Modifies text.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/07/12
 */
public interface TextModifyer {

    /**
     * Returns the modified text.
     * 
     * @param  text        text to modify
     * @param  ignoreWords if the text contains one of these words, they won't
     *                     be modified
     * @return modified text
     */
    public String modify(String text, Collection<String> ignoreWords);

    /**
     * Returns a (short) description what the modifier does modify.
     *
     * @return description
     */
    public String getDescription();
}
