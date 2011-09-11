package org.jphototagger.domain.repository;

import java.util.Collection;
import java.util.List;

import org.jphototagger.domain.keywords.Keyword;


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

    Collection<Keyword> getAllKeywords();

    Collection<Keyword> getChildKeywords(long idParent);

    Collection<Collection<Keyword>> getParentKeywords(String keywordName, KeywordsSelect select);

    List<Keyword> getParentKeywords(Keyword keyword);

    Collection<Keyword> getRootKeywords();

    boolean hasParentChildKeywordWithEqualName(Keyword keyword);

    boolean insertKeyword(Keyword keyword);

    boolean updateKeyword(Keyword keyword);

    int updateRenameAllKeywords(String fromName, String toName);
}
