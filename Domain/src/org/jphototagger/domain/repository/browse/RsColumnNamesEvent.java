package org.jphototagger.domain.repository.browse;

import java.util.Objects;

/**
 * @author Elmar Baumann
 */
public final class RsColumnNamesEvent implements ResultSetBrowserEvent {

    private final Object[] columnNames;

    public RsColumnNamesEvent(Object[] columnNames) {
        this.columnNames = Objects.requireNonNull(columnNames, "columnNames == null");
    }

    @Override
    public void publish(ResultSetBrowser browser) {
        browser.columnNames(columnNames);
    }
}
