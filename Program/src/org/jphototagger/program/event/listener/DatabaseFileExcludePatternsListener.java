package org.jphototagger.program.event.listener;

/**
 * Listens to events in
 * {@link org.jphototagger.program.database.DatabaseFileExcludePatterns}.
 *
 * @author Elmar Baumann
 */
public interface DatabaseFileExcludePatternsListener {

    /**
     * Called if a pattern was inserted into
     * {@link org.jphototagger.program.database.DatabaseFileExcludePatterns}.
     *
     * @param pattern inserted pattern
     */
    void patternInserted(String pattern);

    /**
     * Called if a pattern was deleted from
     * {@link org.jphototagger.program.database.DatabaseFileExcludePatterns}.
     *
     * @param pattern deleted pattern
     */
    void patternDeleted(String pattern);
}
