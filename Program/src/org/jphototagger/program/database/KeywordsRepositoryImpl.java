package org.jphototagger.program.database;

import java.util.Collection;
import java.util.List;

import org.jphototagger.domain.keywords.Keyword;
import org.jphototagger.domain.repository.KeywordsRepository;
import org.jphototagger.domain.repository.KeywordsSelect;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 *
 * @author Elmar Baumann
 */
@ServiceProvider(service = KeywordsRepository.class)
public final class KeywordsRepositoryImpl implements KeywordsRepository {

    private final DatabaseKeywords db = DatabaseKeywords.INSTANCE;

    @Override
    public int deleteAllKeywords() {
        return db.deleteAllKeywords();
    }

    @Override
    public boolean deleteKeywords(Collection<Keyword> keywords) {
        return db.deleteKeywords(keywords);
    }

    @Override
    public boolean existsKeyword(String keyword) {
        return db.existsKeyword(keyword);
    }

    @Override
    public boolean existsRootKeyword(String keyword) {
        return db.existsRootKeyword(keyword);
    }

    @Override
    public Collection<Keyword> getAllKeywords() {
        return db.getAllKeywords();
    }

    @Override
    public Collection<Keyword> getChildKeywords(long idParent) {
        return db.getChildKeywords(idParent);
    }

    @Override
    public Collection<Collection<Keyword>> getParentKeywords(String keywordName, KeywordsSelect select) {
        return db.getParentKeywords(keywordName, select);
    }

    @Override
    public List<Keyword> getParentKeywords(Keyword keyword) {
        return db.getParentKeywords(keyword);
    }

    @Override
    public Collection<Keyword> getRootKeywords() {
        return db.getRootKeywords();
    }

    @Override
    public boolean hasParentChildKeywordWithEqualName(Keyword keyword) {
        return db.hasParentChildKeywordWithEqualName(keyword);
    }

    @Override
    public boolean insertKeyword(Keyword keyword) {
        return db.insertKeyword(keyword);
    }

    @Override
    public boolean updateKeyword(Keyword keyword) {
        return db.updateKeyword(keyword);
    }

    @Override
    public int updateRenameAllKeywords(String fromName, String toName) {
        return db.updateRenameAllKeywords(fromName, toName);
    }
}
