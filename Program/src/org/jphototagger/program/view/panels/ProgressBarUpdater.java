package org.jphototagger.program.view.panels;

import org.openide.util.Lookup;

import org.jphototagger.api.progress.MainWindowProgressBarProvider;
import org.jphototagger.api.progress.ProgressEvent;
import org.jphototagger.api.progress.ProgressListener;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class ProgressBarUpdater implements ProgressListener {

    private final String progressBarString;
    private final Object pBarOwner;
    private final MainWindowProgressBarProvider progressBarProvider = Lookup.getDefault().lookup(MainWindowProgressBarProvider.class);

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

    private void modifyProgressEvent(ProgressEvent evt) {
        evt.setSource(pBarOwner);
        evt.setStringPainted(progressBarString != null);
        evt.setStringToPaint(progressBarString);
    }

    @Override
    public void progressStarted(ProgressEvent evt) {
        modifyProgressEvent(evt);
        progressBarProvider.progressStarted(evt);
    }

    @Override
    public void progressPerformed(final ProgressEvent evt) {
        modifyProgressEvent(evt);
        progressBarProvider.progressPerformed(evt);
    }

    @Override
    public synchronized void progressEnded(final ProgressEvent evt) {
        progressBarProvider.progressEnded(pBarOwner);
    }
}
