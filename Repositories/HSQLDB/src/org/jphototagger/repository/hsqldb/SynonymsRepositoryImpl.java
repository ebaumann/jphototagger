package org.jphototagger.repository.hsqldb;

import java.util.Set;

import org.openide.util.lookup.ServiceProvider;

import org.jphototagger.domain.repository.SynonymsRepository;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = SynonymsRepository.class)
public final class SynonymsRepositoryImpl implements SynonymsRepository {

    @Override
    public int deleteSynonym(String word, String synonym) {
        return SynonymsDatabase.INSTANCE.deleteSynonym(word, synonym);
    }

    @Override
    public int deleteWord(String word) {
        return SynonymsDatabase.INSTANCE.deleteWord(word);
    }

    @Override
    public boolean existsSynonym(String word, String synonym) {
        return SynonymsDatabase.INSTANCE.existsSynonym(word, synonym);
    }

    @Override
    public boolean existsWord(String word) {
        return SynonymsDatabase.INSTANCE.existsWord(word);
    }

    @Override
    public Set<String> findAllWords() {
        return SynonymsDatabase.INSTANCE.getAllWords();
    }

    @Override
    public Set<String> findSynonymsOfWord(String word) {
        return SynonymsDatabase.INSTANCE.getSynonymsOfWord(word);
    }

    @Override
    public int saveSynonym(String word, String synonym) {
        return SynonymsDatabase.INSTANCE.insertSynonym(word, synonym);
    }

    @Override
    public int updateSynonym(String oldSynonym, String newSynonym) {
        return SynonymsDatabase.INSTANCE.updateSynonym(oldSynonym, newSynonym);
    }

    @Override
    public int updateSynonymOfWord(String word, String oldSynonym, String newSynonym) {
        return SynonymsDatabase.INSTANCE.updateSynonymOfWord(word, oldSynonym, newSynonym);
    }

    @Override
    public int updateWord(String oldWord, String newWord) {
        return SynonymsDatabase.INSTANCE.updateWord(oldWord, newWord);
    }
}
