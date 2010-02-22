package de.elmar_baumann.jpt.view.panels;

import de.elmar_baumann.jpt.event.ProgressEvent;
import de.elmar_baumann.jpt.event.listener.ProgressListener;
import javax.swing.JProgressBar;

/**
 *
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-12-18
 */
public final class ProgressBarUpdater implements ProgressListener {

    private final String       progressBarString;
    private       JProgressBar progressBar;

    /**
     *
     * @param progressBarString string to paint on the progress bar or null
     */
    public ProgressBarUpdater(String progressBarString) {
        this.progressBarString = progressBarString;
    }

    private void getProgressBar() {
        if (progressBar != null) return;
        progressBar = ProgressBar.INSTANCE.getResource(this);
    }

    private void updateProgressBar(ProgressEvent evt) {
        getProgressBar();
        if (progressBar != null) {
            progressBar.setMinimum(evt.getMinimum());
            progressBar.setMaximum(evt.getMaximum());
            progressBar.setValue(evt.getValue());
            if (progressBarString != null && !progressBar.isStringPainted()) {
                progressBar.setStringPainted(true);
            }
            if (progressBarString != null && !progressBarString.equals(progressBar.getString())) {
                progressBar.setString(progressBarString);
            }
        }
    }

    @Override
    public void progressStarted(ProgressEvent evt) {
        updateProgressBar(evt);
    }

    @Override
    public void progressPerformed(ProgressEvent evt) {
        updateProgressBar(evt);
    }

    @Override
    public synchronized void progressEnded(ProgressEvent evt) {
        updateProgressBar(evt);
        if (progressBar != null) {
            if (progressBar.isStringPainted()) {
                progressBar.setString("");
            }
            progressBar.setValue(0);
        }
        ProgressBar.INSTANCE.releaseResource(this);
    }

    public synchronized boolean setIndeterminate(boolean indeterminate) {
        if (progressBar != null) {
            progressBar.setIndeterminate(indeterminate);
            return true;
        }
        return false;
    }
}
