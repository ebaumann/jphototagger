package org.jphototagger.domain.repository;

import java.util.Set;

/**
 * @author Elmar Baumann
 */
public interface SynonymsRepository {

    int deleteSynonym(String word, String synonym);

    int deleteWord(String word);

    boolean existsSynonym(String word, String synonym);

    boolean existsWord(String word);

    Set<String> findAllWords();

    Set<String> findSynonymsOfWord(String word);

    int saveSynonym(String word, String synonym);

    int updateSynonym(String oldSynonym, String newSynonym);

    int updateSynonymOfWord(String word, String oldSynonym, String newSynonym);

    int updateWord(String oldWord, String newWord);
}
