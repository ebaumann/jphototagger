package org.jphototagger.program.model;

import javax.swing.DefaultListModel;

import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.jphototagger.domain.filefilter.UserDefinedFileFilter;
import org.jphototagger.domain.repository.UserDefinedFileFiltersRepository;
import org.jphototagger.domain.repository.event.userdefinedfilefilters.UserDefinedFileFilterDeletedEvent;
import org.jphototagger.domain.repository.event.userdefinedfilefilters.UserDefinedFileFilterInsertedEvent;
import org.jphototagger.domain.repository.event.userdefinedfilefilters.UserDefinedFileFilterUpdatedEvent;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.program.database.ConnectionPool;
import org.openide.util.Lookup;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class ListModelUserDefinedFileFilter extends DefaultListModel {

    private static final long serialVersionUID = 6723254193291648654L;
    private final UserDefinedFileFiltersRepository repo = Lookup.getDefault().lookup(UserDefinedFileFiltersRepository.class);

    public ListModelUserDefinedFileFilter() {
        addElements();
        AnnotationProcessor.process(this);
    }

    private void addElements() {
        if (!ConnectionPool.INSTANCE.isInit()) {
            return;
        }

        for (UserDefinedFileFilter filter : repo.getAllUserDefinedFileFilters()) {
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
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                insertFilter(evt.getFilter());
            }
        });
    }

    @EventSubscriber(eventClass = UserDefinedFileFilterDeletedEvent.class)
    public void filterDeleted(final UserDefinedFileFilterDeletedEvent evt) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                deleteFilter(evt.getFilter());
            }
        });
    }

    @EventSubscriber(eventClass = UserDefinedFileFilterUpdatedEvent.class)
    public synchronized void filterUpdated(final UserDefinedFileFilterUpdatedEvent evt) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                updateFilter(evt.getFilter());
            }
        });
    }
}
