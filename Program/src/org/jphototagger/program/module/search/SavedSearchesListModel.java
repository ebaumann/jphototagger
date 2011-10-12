package org.jphototagger.program.module.search;

import java.util.List;

import javax.swing.DefaultListModel;

import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;

import org.openide.util.Lookup;

import org.jphototagger.domain.metadata.search.SavedSearch;
import org.jphototagger.domain.repository.Repository;
import org.jphototagger.domain.repository.SavedSearchesRepository;
import org.jphototagger.domain.repository.event.search.SavedSearchDeletedEvent;
import org.jphototagger.domain.repository.event.search.SavedSearchInsertedEvent;
import org.jphototagger.domain.repository.event.search.SavedSearchRenamedEvent;
import org.jphototagger.domain.repository.event.search.SavedSearchUpdatedEvent;

/**
 * Elements are {@code SavedSearch}es.
 *
 * @author Elmar Baumann
 */
public final class SavedSearchesListModel extends DefaultListModel {

    private static final long serialVersionUID = 1L;
    private final SavedSearchesRepository savedSearchRepo = Lookup.getDefault().lookup(SavedSearchesRepository.class);

    public SavedSearchesListModel() {
        addElements();
        listen();
    }

    private void listen() {
        AnnotationProcessor.process(this);
    }

    private void addElements() {
        Repository repo = Lookup.getDefault().lookup(Repository.class);

        if (repo == null || !repo.isInit()) {
            return;
        }

        List<SavedSearch> savedSearches = savedSearchRepo.findAllSavedSearches();

        for (SavedSearch savedSearch : savedSearches) {
            addElement(savedSearch);
        }
    }

    private void insertSavedSearch(SavedSearch search) {
        if (!contains(search)) {
            addElement(search);
        }
    }

    private void deleteSearch(String name) {
        int index = SavedSearchesUtil.getIndexOfSavedSearch(this, name);

        if (index >= 0) {
            remove(index);
        }
    }

    private void renameSearch(String fromName, String toName) {
        int index = SavedSearchesUtil.getIndexOfSavedSearch(this, fromName);

        if (index >= 0) {
            SavedSearch savedSearch = (SavedSearch) get(index);

            savedSearch.setName(toName);
            fireContentsChanged(this, index, index);
        }
    }

    private void insertSearch(SavedSearch savedSearch) {
        insertSavedSearch(savedSearch);
    }

    private void updateSearch(SavedSearch savedSearch) {
        int index = indexOf(savedSearch);

        if (index >= 0) {
            set(index, savedSearch);
        } else {
            insertSavedSearch(savedSearch);
        }
    }

    @EventSubscriber(eventClass = SavedSearchInsertedEvent.class)
    public void searchInserted(final SavedSearchInsertedEvent evt) {
        insertSearch(evt.getSavedSearch());
    }

    @EventSubscriber(eventClass = SavedSearchUpdatedEvent.class)
    public void searchUpdated(final SavedSearchUpdatedEvent evt) {
        updateSearch(evt.getSavedSearch());
    }

    @EventSubscriber(eventClass = SavedSearchDeletedEvent.class)
    public void searchDeleted(final SavedSearchDeletedEvent evt) {
        deleteSearch(evt.getSearchName());
    }

    @EventSubscriber(eventClass = SavedSearchRenamedEvent.class)
    public void searchRenamed(final SavedSearchRenamedEvent evt) {
        String fromName = evt.getFromName();
        String toName = evt.getToName();

        renameSearch(fromName, toName);
    }
}
