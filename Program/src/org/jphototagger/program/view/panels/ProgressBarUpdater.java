package org.jphototagger.program.view.panels;

import org.jphototagger.program.event.listener.ProgressListener;
import org.jphototagger.program.event.ProgressEvent;
import javax.swing.JProgressBar;
import org.jphototagger.lib.awt.EventQueueUtil;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class ProgressBarUpdater implements ProgressListener {
    private final String progressBarString;
    private JProgressBar progressBar;
    private final Object pBarOwner;

    /**
     *
     * @param progressBarOwner  owner of the progress bar
     * @param progressBarString string to paint on the progress bar or null
     */
    public ProgressBarUpdater(Object progressBarOwner, String progressBarString) {
        if (progressBarOwner == null) {
            throw new NullPointerException("progressBarOwner == null");
        }

        this.pBarOwner = progressBarOwner;
        this.progressBarString = progressBarString;
    }

    private synchronized void getProgressBar() {
        if (progressBar != null) {
            return;
        }

        progressBar = ProgressBar.INSTANCE.getResource(pBarOwner);
    }

    private synchronized void updateProgressBar(final ProgressEvent evt) {
        getProgressBar();
        EventQueueUtil.invokeInDispatchThread(new Runnable() {
            @Override
            public void run() {
                if (progressBar != null) {
                    progressBar.setMinimum(evt.getMinimum());
                    progressBar.setMaximum(evt.getMaximum());
                    progressBar.setValue(evt.getValue());

                    if ((progressBarString != null) &&!progressBar.isStringPainted()) {
                        progressBar.setStringPainted(true);
                    }

                    if ((progressBarString != null) &&!progressBarString.equals(progressBar.getString())) {
                        progressBar.setString(progressBarString);
                    }
                }
            }
        });
    }

    @Override
    public void progressStarted(ProgressEvent evt) {
        updateProgressBar(evt);
    }

    @Override
    public void progressPerformed(final ProgressEvent evt) {
        updateProgressBar(evt);
    }

    @Override
    public synchronized void progressEnded(final ProgressEvent evt) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {
            @Override
            public void run() {
                if (progressBar != null) {
                    if (progressBar.isStringPainted()) {
                        progressBar.setString("");
                    }

                    progressBar.setValue(0);
                    ProgressBar.INSTANCE.releaseResource(pBarOwner);
                    progressBar = null;
                }
            }
        });
    }
}
