package org.jphototagger.domain.repository;

import java.util.List;
import org.jphototagger.domain.wordsets.Wordset;

/**
 * @author Elmar Baumann
 */
public interface WordsetsRepository {

    List<Wordset> findAll();

    Wordset find(String wordsetName);

    List<String> findAllWordsetNames();

    String findWordsetNameById(long id);

    long findWordsetId(String wordsetName);

    boolean insert(Wordset wordset);

    boolean update(Wordset wordset);

    boolean remove(String wordsetName);

    boolean existsWordset(String wordsetName);

    boolean addToWords(String wordsetName, String word);

    boolean removeFromWords(String wordsetName, String word);

    boolean renameWord(String wordsetName, String oldWord, String newWord);

    boolean renameWordset(String oldWordsetName, String newWordsetName);
}
