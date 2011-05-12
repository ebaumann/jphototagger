package org.jphototagger.program.model;

import org.jphototagger.program.data.UserDefinedFileFilter;
import org.jphototagger.program.database.ConnectionPool;
import org.jphototagger.program.database.DatabaseUserDefinedFileFilters;
import org.jphototagger.program.event.listener.DatabaseUserDefinedFileFiltersListener;


import javax.swing.DefaultListModel;
import org.jphototagger.lib.awt.EventQueueUtil;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class ListModelUserDefinedFileFilter extends DefaultListModel
        implements DatabaseUserDefinedFileFiltersListener {
    private static final long serialVersionUID = 6723254193291648654L;

    public ListModelUserDefinedFileFilter() {
        addElements();
        DatabaseUserDefinedFileFilters.INSTANCE.addListener(this);
    }

    private void addElements() {
        if (!ConnectionPool.INSTANCE.isInit()) {
            return;
        }

        for (UserDefinedFileFilter filter : DatabaseUserDefinedFileFilters.INSTANCE.getAll()) {
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

    @Override
    public void filterInserted(final UserDefinedFileFilter filter) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {
            @Override
            public void run() {
                insertFilter(filter);
            }
        });
    }

    @Override
    public void filterDeleted(final UserDefinedFileFilter filter) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {
            @Override
            public void run() {
                deleteFilter(filter);
            }
        });
    }

    @Override
    public void filterUpdated(final UserDefinedFileFilter filter) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {
            @Override
            public void run() {
                updateFilter(filter);
            }
        });
    }
}
