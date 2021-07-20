package org.jphototagger.lib.util;

import org.jphototagger.api.concurrent.Cancelable;
import org.jphototagger.api.progress.ProgressEvent;
import org.jphototagger.api.progress.ProgressHandle;
import org.jphototagger.api.progress.ProgressHandleFactory;
import org.jphototagger.api.progress.ProgressListener;
import org.openide.util.Lookup;

/**
 * @author Elmar Baumann
 */
public final class ProgressBarUpdater implements ProgressListener {

    private final String progressBarString;
    private final Object source;
    private ProgressHandle progressHandle;

    public ProgressBarUpdater(Object source, String progressBarString) {
        if (source == null) {
            throw new NullPointerException("source == null");
        }
        this.source = source;
        this.progressBarString = progressBarString;
    }

    private void modifyProgressEvent(ProgressEvent evt) {
        evt.setSource(source);
        evt.setStringPainted(progressBarString != null);
        evt.setStringToPaint(progressBarString);
    }

    @Override
    public void progressStarted(ProgressEvent evt) {
        modifyProgressEvent(evt);
        ProgressHandleFactory phFactory = Lookup.getDefault().lookup(ProgressHandleFactory.class);
        progressHandle = source instanceof Cancelable
                ? phFactory.createProgressHandle((Cancelable) source)
                : phFactory.createProgressHandle();
        progressHandle.progressStarted(evt);
    }

    @Override
    public void progressPerformed(final ProgressEvent evt) {
        modifyProgressEvent(evt);
        progressHandle.progressPerformed(evt);
    }

    /**
     * @param evt can be null, will be ignored
     */
    @Override
    public synchronized void progressEnded(final ProgressEvent evt) {
        progressHandle.progressEnded();
    }
}
