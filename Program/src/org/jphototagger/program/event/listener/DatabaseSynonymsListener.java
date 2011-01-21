package org.jphototagger.program.event.listener;

/**
 * Listens to events in
 * {@link org.jphototagger.program.database.DatabaseSynonyms}.
 *
 * @author Elmar Baumann
 */
public interface DatabaseSynonymsListener {

    /**
     * Called if a synonym of a word was deleted from
     * {@link org.jphototagger.program.database.DatabaseSynonyms}.
     *
     * @param word    word
     * @param synonym delted synonym
     */
    void synonymOfWordDeleted(String word, String synonym);

    /**
     * Called if a synonym was inserted into
     * {@link org.jphototagger.program.database.DatabaseSynonyms}.
     *
     * @param word    word for which the synonym is a synonym
     * @param synonym inserted synonym
     */
    void synonymInserted(String word, String synonym);

    /**
     * Called if a synonym of a word was renamed in
     * {@link org.jphototagger.program.database.DatabaseSynonyms}.
     *
     * @param word           word
     * @param oldSynonymName old name of the synonym
     * @param newSynonymName new name of the synonym
     */
    void synonymOfWordRenamed(String word, String oldSynonymName,
                                     String newSynonymName);

    /**
     * Called if a synonym was renamed in
     * {@link org.jphototagger.program.database.DatabaseSynonyms}.
     *
     * @param oldSynonymName old name of the synonym
     * @param newSynonymName new name of the synonym
     */
    void synonymRenamed(String oldSynonymName, String newSynonymName);

    /**
     * Called if a word was deleted from
     * {@link org.jphototagger.program.database.DatabaseSynonyms}.
     *
     * @param word deleted word
     */
    void wordDeleted(String word);

    /**
     * Called if a synonym word renamed in
     * {@link org.jphototagger.program.database.DatabaseSynonyms}.
     *
     * @param fromName old name of the word
     * @param toName   new name of the word
     */
    void wordRenamed(String fromName, String toName);
}
