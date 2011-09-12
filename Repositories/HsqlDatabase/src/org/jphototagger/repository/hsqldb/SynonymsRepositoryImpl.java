package org.jphototagger.repository.hsqldb;

import java.util.Set;

import org.jphototagger.domain.repository.SynonymsRepository;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 *
 * @author Elmar Baumann
 */
@ServiceProvider(service = SynonymsRepository.class)
public final class SynonymsRepositoryImpl implements SynonymsRepository {

    private final DatabaseSynonyms db = DatabaseSynonyms.INSTANCE;

    @Override
    public int deleteSynonym(String word, String synonym) {
        return db.deleteSynonym(word, synonym);
    }

    @Override
    public int deleteWord(String word) {
        return db.deleteWord(word);
    }

    @Override
    public boolean existsSynonym(String word, String synonym) {
        return db.existsSynonym(word, synonym);
    }

    @Override
    public boolean existsWord(String word) {
        return db.existsWord(word);
    }

    @Override
    public Set<String> findAllWords() {
        return db.getAllWords();
    }

    @Override
    public Set<String> findSynonymsOfWord(String word) {
        return db.getSynonymsOfWord(word);
    }

    @Override
    public int saveSynonym(String word, String synonym) {
        return db.insertSynonym(word, synonym);
    }

    @Override
    public int updateSynonym(String oldSynonym, String newSynonym) {
        return db.updateSynonym(oldSynonym, newSynonym);
    }

    @Override
    public int updateSynonymOfWord(String word, String oldSynonym, String newSynonym) {
        return db.updateSynonymOfWord(word, oldSynonym, newSynonym);
    }

    @Override
    public int updateWord(String oldWord, String newWord) {
        return db.updateWord(oldWord, newWord);
    }
}
