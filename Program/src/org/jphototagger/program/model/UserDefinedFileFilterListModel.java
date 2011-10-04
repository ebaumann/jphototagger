package org.jphototagger.program.model;

import javax.swing.DefaultListModel;

import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;

import org.openide.util.Lookup;

import org.jphototagger.domain.filefilter.UserDefinedFileFilter;
import org.jphototagger.domain.repository.Repository;
import org.jphototagger.domain.repository.UserDefinedFileFiltersRepository;
import org.jphototagger.domain.repository.event.userdefinedfilefilters.UserDefinedFileFilterDeletedEvent;
import org.jphototagger.domain.repository.event.userdefinedfilefilters.UserDefinedFileFilterInsertedEvent;
import org.jphototagger.domain.repository.event.userdefinedfilefilters.UserDefinedFileFilterUpdatedEvent;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class UserDefinedFileFilterListModel extends DefaultListModel {

    private static final long serialVersionUID = 6723254193291648654L;
    private final UserDefinedFileFiltersRepository udffRepo = Lookup.getDefault().lookup(UserDefinedFileFiltersRepository.class);

    public UserDefinedFileFilterListModel() {
        addElements();
        AnnotationProcessor.process(this);
    }

    private void addElements() {
        Repository repo = Lookup.getDefault().lookup(Repository.class);

        if (repo == null || !repo.isInit()) {
            return;
        }

        for (UserDefinedFileFilter filter : udffRepo.findAllUserDefinedFileFilters()) {
            addElement(filter);
        }
    }

    private void updateFilter(UserDefinedFileFilter filter) {
        int index = indexOf(filter);

        if (index >= 0) {
            ((UserDefinedFileFilter) getElementAt(index)).set(filter);
            fireContentsChanged(this, index, index);
        }
    }

    private void deleteFilter(UserDefinedFileFilter filter) {
        removeElement(filter);
    }

    private void insertFilter(UserDefinedFileFilter filter) {
        addElement(filter);
    }

    @EventSubscriber(eventClass = UserDefinedFileFilterInsertedEvent.class)
    public void filterInserted(final UserDefinedFileFilterInsertedEvent evt) {
        insertFilter(evt.getFilter());
    }

    @EventSubscriber(eventClass = UserDefinedFileFilterDeletedEvent.class)
    public void filterDeleted(final UserDefinedFileFilterDeletedEvent evt) {
        deleteFilter(evt.getFilter());
    }

    @EventSubscriber(eventClass = UserDefinedFileFilterUpdatedEvent.class)
    public synchronized void filterUpdated(final UserDefinedFileFilterUpdatedEvent evt) {
        updateFilter(evt.getFilter());
    }
}
