package org.jphototagger.domain.repository;

import java.util.Collection;
import java.util.List;

import org.jphototagger.domain.keywords.Keyword;
import org.jphototagger.domain.keywords.KeywordType;

/**
 *
 *
 * @author Elmar Baumann
 */
public interface KeywordsRepository {

    int deleteAllKeywords();

    boolean deleteKeywords(Collection<Keyword> keywords);

    boolean existsKeyword(String keyword);

    boolean existsRootKeyword(String keyword);

    Collection<Keyword> findAllKeywords();

    Collection<Keyword> findChildKeywords(long idParent);

    Collection<Collection<Keyword>> findParentKeywords(String keywordName, KeywordType select);

    List<Keyword> findParentKeywords(Keyword keyword);

    Collection<Keyword> findRootKeywords();

    boolean hasParentChildKeywordWithEqualName(Keyword keyword);

    boolean saveKeyword(Keyword keyword);

    boolean updateKeyword(Keyword keyword);

    int updateRenameAllKeywords(String fromName, String toName);
}
