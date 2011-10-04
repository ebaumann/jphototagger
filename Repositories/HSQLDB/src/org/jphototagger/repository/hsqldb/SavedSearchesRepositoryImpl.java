package org.jphototagger.repository.hsqldb;

import java.util.List;

import org.openide.util.lookup.ServiceProvider;

import org.jphototagger.domain.metadata.search.SavedSearch;
import org.jphototagger.domain.repository.SavedSearchesRepository;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = SavedSearchesRepository.class)
public final class SavedSearchesRepositoryImpl implements SavedSearchesRepository {

    @Override
    public boolean deleteSavedSearch(String searchName) {
        return SavedSearchesDatabase.INSTANCE.delete(searchName);
    }

    @Override
    public boolean existsSavedSearch(String searchName) {
        return SavedSearchesDatabase.INSTANCE.exists(searchName);
    }

    @Override
    public SavedSearch findSavedSearch(String searchName) {
        return SavedSearchesDatabase.INSTANCE.find(searchName);
    }

    @Override
    public List<SavedSearch> findAllSavedSearches() {
        return SavedSearchesDatabase.INSTANCE.getAll();
    }

    @Override
    public int getSavedSearchesCount() {
        return SavedSearchesDatabase.INSTANCE.getCount();
    }

    @Override
    public boolean saveSavedSearch(SavedSearch savedSearch) {
        return SavedSearchesDatabase.INSTANCE.insert(savedSearch);
    }

    @Override
    public void tagSearchesIfStmtContains(String what, String tag) {
        SavedSearchesDatabase.INSTANCE.tagSearchesIfStmtContains(what, tag);
    }

    @Override
    public boolean updateSavedSearch(SavedSearch savedSearch) {
        return SavedSearchesDatabase.INSTANCE.update(savedSearch);
    }

    @Override
    public boolean updateRenameSavedSearch(String fromSearchName, String toSearchName) {
        return SavedSearchesDatabase.INSTANCE.updateRename(fromSearchName, toSearchName);
    }
}
