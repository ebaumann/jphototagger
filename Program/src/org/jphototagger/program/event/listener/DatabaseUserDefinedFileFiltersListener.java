package org.jphototagger.program.event.listener;

import org.jphototagger.program.data.UserDefinedFileFilter;

/**
 *
 *
 * @author Elmar Baumann
 */
public interface DatabaseUserDefinedFileFiltersListener {
    void filterInserted(UserDefinedFileFilter filter);

    void filterDeleted(UserDefinedFileFilter filter);

    void filterUpdated(UserDefinedFileFilter filter);
}
