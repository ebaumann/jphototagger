package org.jphototagger.domain.event.listener;

import java.awt.Component;
import org.jphototagger.api.progress.ProgressEvent;
import org.jphototagger.api.progress.ProgressListener;
import org.jphototagger.lib.awt.EventQueueUtil;

/**
 * Adds, removes and notifies {@code ProgressListener} instances.
 *
 * @author Elmar Baumann
 */
public final class ProgressListenerSupport extends ListenerSupport<ProgressListener> {

    /**
     * Calls on every added progress listener
     * {@code ProgressListener#progressStarted(ProgressEvent)}.
     *
     * @param event progress event
     */
    public void notifyStarted(final ProgressEvent event) {
        if (event == null) {
            throw new NullPointerException("event == null");
        }

        for (final ProgressListener listener : listeners) {
            if (listener instanceof Component) {
                EventQueueUtil.invokeInDispatchThread(new Runnable() {

                    @Override
                    public void run() {
                        listener.progressStarted(event);
                    }
                });
            } else {
                listener.progressStarted(event);
            }
        }
    }

    /**
     * Calls on every added progress listener
     * {@code ProgressListener#progressPerformed(ProgressEvent)}.
     *
     * @param  event progress event
     * @return       true if one of the of the events returns
     *               {@code ProgressEvent#isCancel()}
     */
    public boolean notifyPerformed(final ProgressEvent event) {
        if (event == null) {
            throw new NullPointerException("event == null");
        }

        boolean cancel = false;

        for (final ProgressListener listener : listeners) {
            if (listener instanceof Component) {
                EventQueueUtil.invokeInDispatchThread(new Runnable() {

                    @Override
                    public void run() {
                        listener.progressPerformed(event);
                    }
                });
            } else {
                listener.progressPerformed(event);
            }
        }

        return cancel;
    }

    /**
     * Calls on every added progress listener
     * {@code ProgressListener#progressEnded(ProgressEvent)}.
     *
     * @param event progress event
     */
    public void notifyEnded(final ProgressEvent event) {
        if (event == null) {
            throw new NullPointerException("event == null");
        }

        for (final ProgressListener listener : listeners) {
            if (listener instanceof Component) {
                EventQueueUtil.invokeInDispatchThread(new Runnable() {

                    @Override
                    public void run() {
                        listener.progressEnded(event);
                    }
                });
            } else {
                listener.progressEnded(event);
            }
        }
    }
}
