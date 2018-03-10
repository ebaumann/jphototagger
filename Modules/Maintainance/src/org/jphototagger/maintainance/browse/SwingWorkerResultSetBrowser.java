package org.jphototagger.maintainance.browse;

import org.jphototagger.domain.repository.browse.ResultSetBrowser;
import org.jphototagger.domain.repository.browse.ResultSetBrowserEvent;
import java.util.List;
import java.util.Objects;
import javax.swing.SwingWorker;
import org.jphototagger.api.function.Consumer;
import org.jphototagger.domain.repository.RepositoryMaintainance;
import org.openide.util.Lookup;

/**
 * Browses
 * {@link RepositoryMaintainance#browse(java.lang.String, java.util.Collection)}
 * in a background thread and publishes chunks of {@link ResultSetBrowserEvent}
 * in the Event dispatch thread.
 *
 * @author Elmar Baumann
 */
public class SwingWorkerResultSetBrowser extends SwingWorker<ResultSetBrowser.Result, ResultSetBrowserEvent> implements Consumer<ResultSetBrowserEvent> {

    private final String sql;
    private final ResultSetBrowser browser;
    private ResultSetBrowser.Result result;

    protected SwingWorkerResultSetBrowser(String sql, ResultSetBrowser browser) {
        this.sql = Objects.requireNonNull(sql, "sql == null");
        this.browser = Objects.requireNonNull(browser, "browser == null");
    }

    @Override
    protected ResultSetBrowser.Result doInBackground() throws Exception {
        RepositoryMaintainance rm = Lookup.getDefault().lookup(RepositoryMaintainance.class);
        rm.browse(sql, this);
        return result;
    }

    @Override
    protected void process(List<ResultSetBrowserEvent> chunks) {
        for (ResultSetBrowserEvent event : chunks) {
            event.publish(browser);
        }
    }

    @Override
    public void accept(ResultSetBrowserEvent t) {
        publish(t);
    }
}
