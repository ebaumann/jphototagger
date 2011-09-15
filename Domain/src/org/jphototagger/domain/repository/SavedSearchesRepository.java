package org.jphototagger.domain.repository;

import java.util.List;
import org.jphototagger.domain.metadata.search.SavedSearch;

/**
 *
 *
 * @author Elmar Baumann
 */
public interface SavedSearchesRepository {

    boolean deleteSavedSearch(String searchName);

    boolean existsSavedSearch(String searchName);

    SavedSearch findSavedSearch(String searchName);

    List<SavedSearch> findAllSavedSearches();

    int getSavedSearchesCount();

    boolean saveSavedSearch(SavedSearch savedSearch);

    void tagSearchesIfStmtContains(String what, String tag);

    boolean updateSavedSearch(SavedSearch savedSearch);

    boolean updateRenameSavedSearch(String fromSearchName, String toSearchName);
}
