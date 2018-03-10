package org.jphototagger.domain.repository.browse;

import org.jphototagger.domain.repository.browse.ResultSetBrowser;
import org.jphototagger.domain.repository.browse.ResultSetBrowserEvent;
import java.util.Objects;

/**
 * @author Elmar Baumann
 */
public final class RsRowEvent implements ResultSetBrowserEvent {

    private final Object[] rowData;

    public RsRowEvent(Object[] rowData) {
        this.rowData = Objects.requireNonNull(rowData, "rowData == null");
    }

    @Override
    public void publish(ResultSetBrowser browser) {
        browser.row(rowData);
    }
}
