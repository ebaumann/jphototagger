package org.jphototagger.program.app.ui;

import java.util.Collection;

/**
 * @author Elmar Baumann
 */
public interface Suggest {

    /**
     * Suggests text.
     *
     * @param  input text for searching suggestions
     * @return text suggestions for that text
     */
    Collection<String> suggest(String input);

    /**
     * Returns a description what this class does.
     *
     * @return description
     */
    String getDescription();
}
