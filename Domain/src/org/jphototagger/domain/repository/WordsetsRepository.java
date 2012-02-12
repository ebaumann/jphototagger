package org.jphototagger.domain.repository;

import java.util.List;

import org.jphototagger.domain.wordsets.Wordset;

/**
 * @author Elmar Baumann
 */
public interface WordsetsRepository {

    List<Wordset> findAll();

    boolean insert(Wordset wordset);

    boolean remove(String wordsetName);

    boolean existsWordset(String wordsetName);

    boolean addToWords(String wordsetName, String word);

    boolean removeFromWords(String wordsetName, String word);

    boolean updateWord(String wordsetName, String oldWord, String newWord);
}
