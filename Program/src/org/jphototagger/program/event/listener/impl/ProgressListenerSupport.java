package org.jphototagger.program.event.listener.impl;

import org.jphototagger.program.event.listener.ProgressListener;
import org.jphototagger.program.event.ProgressEvent;

import java.awt.Component;
import java.awt.EventQueue;

/**
 * Adds, removes and notifies {@link ProgressListener} instances.
 *
 * @author Elmar Baumann
 */
public final class ProgressListenerSupport extends ListenerSupport<ProgressListener> {

    /**
     * Calls on every added progress listener
     * {@link ProgressListener#progressStarted(ProgressEvent)}.
     *
     * @param event progress event
     */
    public void notifyStarted(final ProgressEvent event) {
        if (event == null) {
            throw new NullPointerException("event == null");
        }

        for (final ProgressListener listener : listeners) {
            if (listener instanceof Component) {
                EventQueue.invokeLater(new Runnable() {
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
     * {@link ProgressListener#progressPerformed(ProgressEvent)}.
     *
     * @param  event progress event
     * @return       true if one of the of the events returns
     *               {@link ProgressEvent#isCancel()}
     */
    public boolean notifyPerformed(final ProgressEvent event) {
        if (event == null) {
            throw new NullPointerException("event == null");
        }

        boolean cancel = false;

        for (final ProgressListener listener : listeners) {
            if (listener instanceof Component) {
                EventQueue.invokeLater(new Runnable() {
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
     * {@link ProgressListener#progressEnded(ProgressEvent)}.
     *
     * @param event progress event
     */
    public void notifyEnded(final ProgressEvent event) {
        if (event == null) {
            throw new NullPointerException("event == null");
        }

        for (final ProgressListener listener : listeners) {
            if (listener instanceof Component) {
                EventQueue.invokeLater(new Runnable() {
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
