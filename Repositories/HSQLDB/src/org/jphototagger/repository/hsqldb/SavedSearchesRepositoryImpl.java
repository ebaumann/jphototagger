package org.jphototagger.repository.hsqldb;

import java.util.List;
import org.jphototagger.domain.metadata.search.SavedSearch;
import org.jphototagger.domain.repository.SavedSearchesRepository;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 *
 * @author Elmar Baumann
 */
@ServiceProvider(service = SavedSearchesRepository.class)
public final class SavedSearchesRepositoryImpl implements SavedSearchesRepository {

    private final SavedSearchesDatabase db = SavedSearchesDatabase.INSTANCE;

    @Override
    public boolean deleteSavedSearch(String searchName) {
        return db.delete(searchName);
    }

    @Override
    public boolean existsSavedSearch(String searchName) {
        return db.exists(searchName);
    }

    @Override
    public SavedSearch findSavedSearch(String searchName) {
        return db.find(searchName);
    }

    @Override
    public List<SavedSearch> findAllSavedSearches() {
        return db.getAll();
    }

    @Override
    public int getSavedSearchesCount() {
        return db.getCount();
    }

    @Override
    public boolean saveSavedSearch(SavedSearch savedSearch) {
        return db.insert(savedSearch);
    }

    @Override
    public void tagSearchesIfStmtContains(String what, String tag) {
        db.tagSearchesIfStmtContains(what, tag);
    }

    @Override
    public boolean updateSavedSearch(SavedSearch savedSearch) {
        return db.update(savedSearch);
    }

    @Override
    public boolean updateRenameSavedSearch(String fromSearchName, String toSearchName) {
        return db.updateRename(fromSearchName, toSearchName);
    }
}
