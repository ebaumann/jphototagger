package org.jphototagger.repository.hsqldb;

import java.util.Collection;
import java.util.List;

import org.openide.util.lookup.ServiceProvider;

import org.jphototagger.domain.metadata.keywords.Keyword;
import org.jphototagger.domain.metadata.keywords.KeywordType;
import org.jphototagger.domain.repository.KeywordsRepository;

/**
 *
 *
 * @author Elmar Baumann
 */
@ServiceProvider(service = KeywordsRepository.class)
public final class KeywordsRepositoryImpl implements KeywordsRepository {

    private final KeywordsDatabase db = KeywordsDatabase.INSTANCE;

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
    public Collection<Keyword> findAllKeywords() {
        return db.getAllKeywords();
    }

    @Override
    public Collection<Keyword> findChildKeywords(long idParent) {
        return db.getChildKeywords(idParent);
    }

    @Override
    public Collection<Collection<Keyword>> findParentKeywords(String keywordName, KeywordType select) {
        return db.getParentKeywords(keywordName, select);
    }

    @Override
    public List<Keyword> findParentKeywords(Keyword keyword) {
        return db.getParentKeywords(keyword);
    }

    @Override
    public Collection<Keyword> findRootKeywords() {
        return db.getRootKeywords();
    }

    @Override
    public boolean hasParentChildKeywordWithEqualName(Keyword keyword) {
        return db.hasParentChildKeywordWithEqualName(keyword);
    }

    @Override
    public boolean saveKeyword(Keyword keyword) {
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
