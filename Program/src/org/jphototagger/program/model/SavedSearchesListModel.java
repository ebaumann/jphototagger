package org.jphototagger.program.model;

import java.util.List;

import javax.swing.DefaultListModel;

import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.jphototagger.domain.metadata.search.SavedSearch;
import org.jphototagger.domain.repository.Repository;
import org.jphototagger.domain.repository.SavedSearchesRepository;
import org.jphototagger.domain.repository.event.search.SavedSearchDeletedEvent;
import org.jphototagger.domain.repository.event.search.SavedSearchInsertedEvent;
import org.jphototagger.domain.repository.event.search.SavedSearchRenamedEvent;
import org.jphototagger.domain.repository.event.search.SavedSearchUpdatedEvent;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.program.helper.SavedSearchesHelper;
import org.openide.util.Lookup;

/**
 * Elements are {@link SavedSearch}es.
 *
 * @author Elmar Baumann
 */
public final class SavedSearchesListModel extends DefaultListModel {

    private static final long serialVersionUID = 1979666986802551310L;
    private final SavedSearchesRepository savedSearchRepo = Lookup.getDefault().lookup(SavedSearchesRepository.class);

    public SavedSearchesListModel() {
        addElements();
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
        int index = SavedSearchesHelper.getIndexOfSavedSearch(this, name);

        if (index >= 0) {
            remove(index);
        }
    }

    private void renameSearch(String fromName, String toName) {
        int index = SavedSearchesHelper.getIndexOfSavedSearch(this, fromName);

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
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                insertSearch(evt.getSavedSearch());
            }
        });
    }

    @EventSubscriber(eventClass = SavedSearchUpdatedEvent.class)
    public void searchUpdated(final SavedSearchUpdatedEvent evt) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                updateSearch(evt.getSavedSearch());
            }
        });
    }

    @EventSubscriber(eventClass = SavedSearchDeletedEvent.class)
    public void searchDeleted(final SavedSearchDeletedEvent evt) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                deleteSearch(evt.getSearchName());
            }
        });
    }

    @EventSubscriber(eventClass = SavedSearchRenamedEvent.class)
    public void searchRenamed(final SavedSearchRenamedEvent evt) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                String fromName = evt.getFromName();
                String toName = evt.getToName();

                renameSearch(fromName, toName);
            }
        });
    }
}
