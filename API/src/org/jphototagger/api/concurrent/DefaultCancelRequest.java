package org.jphototagger.api.concurrent;

/**
 * @author Elmar Baumann
 */
public final class DefaultCancelRequest implements CancelRequest {

    private boolean cancel;

    public synchronized void setCancel(boolean cancel) {
        this.cancel = cancel;
    }

    @Override
    public synchronized boolean isCancel() {
        return cancel;
    }
}
