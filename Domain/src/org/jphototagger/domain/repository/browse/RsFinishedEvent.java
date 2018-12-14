package org.jphototagger.domain.repository.browse;

import java.util.Objects;

/**
 * @author Elmar Baumann
 */
public final class RsFinishedEvent implements ResultSetBrowserEvent {

    private final ResultSetBrowser.Result result;

    public RsFinishedEvent(ResultSetBrowser.Result result) {
        this.result = Objects.requireNonNull(result, "result == null");
    }

    @Override
    public void publish(ResultSetBrowser browser) {
        browser.finished(result);
    }
}
