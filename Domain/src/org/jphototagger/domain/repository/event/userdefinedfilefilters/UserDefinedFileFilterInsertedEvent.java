package org.jphototagger.domain.repository.event.userdefinedfilefilters;

import org.jphototagger.domain.filefilter.UserDefinedFileFilter;

/**
 * @author  Elmar Baumann
 */
public final class UserDefinedFileFilterInsertedEvent {

    private final Object source;
    private final UserDefinedFileFilter filter;

    public UserDefinedFileFilterInsertedEvent(Object source, UserDefinedFileFilter filter) {
        if (filter == null) {
            throw new NullPointerException("filter == null");
        }

        this.source = source;
        this.filter = filter;
    }

    public UserDefinedFileFilter getFilter() {
        return filter;
    }

    public Object getSource() {
        return source;
    }
}
