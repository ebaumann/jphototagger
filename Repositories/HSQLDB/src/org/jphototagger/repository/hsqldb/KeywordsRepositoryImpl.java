package org.jphototagger.repository.hsqldb;

import java.util.Collection;
import java.util.List;

import org.openide.util.lookup.ServiceProvider;

import org.jphototagger.domain.metadata.keywords.Keyword;
import org.jphototagger.domain.metadata.keywords.KeywordType;
import org.jphototagger.domain.repository.KeywordsRepository;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = KeywordsRepository.class)
public final class KeywordsRepositoryImpl implements KeywordsRepository {

    @Override
    public int deleteAllKeywords() {
        return KeywordsDatabase.INSTANCE.deleteAllKeywords();
    }

    @Override
    public boolean deleteKeywords(Collection<Keyword> keywords) {
        return KeywordsDatabase.INSTANCE.deleteKeywords(keywords);
    }

    @Override
    public boolean existsKeyword(String keyword) {
        return KeywordsDatabase.INSTANCE.existsKeyword(keyword);
    }

    @Override
    public boolean existsRootKeyword(String keyword) {
        return KeywordsDatabase.INSTANCE.existsRootKeyword(keyword);
    }

    @Override
    public Collection<Keyword> findAllKeywords() {
        return KeywordsDatabase.INSTANCE.getAllKeywords();
    }

    @Override
    public Collection<Keyword> findChildKeywords(long idParent) {
        return KeywordsDatabase.INSTANCE.getChildKeywords(idParent);
    }

    @Override
    public Collection<Collection<Keyword>> findParentKeywords(String keywordName, KeywordType select) {
        return KeywordsDatabase.INSTANCE.getParentKeywords(keywordName, select);
    }

    @Override
    public List<Keyword> findParentKeywords(Keyword keyword) {
        return KeywordsDatabase.INSTANCE.getParentKeywords(keyword);
    }

    @Override
    public Collection<Keyword> findRootKeywords() {
        return KeywordsDatabase.INSTANCE.getRootKeywords();
    }

    @Override
    public boolean hasParentChildKeywordWithEqualName(Keyword keyword) {
        return KeywordsDatabase.INSTANCE.hasParentChildKeywordWithEqualName(keyword);
    }

    @Override
    public boolean saveKeyword(Keyword keyword) {
        return KeywordsDatabase.INSTANCE.insertKeyword(keyword);
    }

    @Override
    public boolean updateKeyword(Keyword keyword) {
        return KeywordsDatabase.INSTANCE.updateKeyword(keyword);
    }

    @Override
    public int updateRenameAllKeywords(String fromName, String toName) {
        return KeywordsDatabase.INSTANCE.updateRenameAllKeywords(fromName, toName);
    }
}
