package org.jphototagger.domain.event.listener;

import org.jphototagger.domain.filefilter.UserDefinedFileFilter;

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
