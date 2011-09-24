package org.jphototagger.program.view.panels;

import javax.swing.JProgressBar;

import org.jphototagger.api.progress.ProgressEvent;
import org.openide.util.lookup.ServiceProvider;

import org.jphototagger.api.progress.MainWindowProgressBarProvider;
import org.jphototagger.lib.awt.EventQueueUtil;

/**
 *
 *
 * @author Elmar Baumann
 */
@ServiceProvider(service = MainWindowProgressBarProvider.class)
public final class ProgressBarProviderImpl implements MainWindowProgressBarProvider {

    private Object progressBarOwner;
    private JProgressBar progressBar;
    private final Object monitor = new Object();

    @Override
    public boolean isDisplayProgressOfSource(Object source) {
        synchronized (monitor) {
            return source == progressBarOwner;
        }
    }

    @Override
    public void progressStarted(ProgressEvent evt) {
        synchronized (monitor) {
            performProgressForEvent(evt);
        }
    }

    @Override
    public void progressPerformed(ProgressEvent evt) {
        synchronized (monitor) {
            performProgressForEvent(evt);
        }
    }

    @Override
    public void progressEnded(ProgressEvent evt) {
        synchronized (monitor) {
            final Object eventSource = evt.getSource();
            if (eventSource == progressBarOwner) {
                EventQueueUtil.invokeInDispatchThread(new Runnable() {

                    @Override
                    public void run() {
                        synchronized (monitor) {
                            progressBar.setValue(0);
                            progressBar.setString("");
                            progressBar.setStringPainted(false);
                            progressBar.setIndeterminate(false);
                            progressBarOwner = null;
                            progressBar = null;
                            ProgressBar.INSTANCE.releaseResource(eventSource);
                        }
                    }
                });
            }
        }
    }

    private void performProgressForEvent(ProgressEvent evt) {
        ensureEventSourceIsNotNull(evt);
        Object eventSource = evt.getSource();
        if (otherEventSourceOwnsProgressBar(eventSource)) {
            return;
        }
        acquireProgressBarForEventSource(eventSource);
        setEventToProgressBar(evt);
    }

    private void ensureEventSourceIsNotNull(ProgressEvent evt) {
        if (evt.getSource() == null) {
            throw new IllegalArgumentException("Event source is null: " + evt);
        }
    }

    private boolean otherEventSourceOwnsProgressBar(Object eventSource) {
        return progressBarOwner != null && progressBarOwner != eventSource;
    }

    private void acquireProgressBarForEventSource(Object eventSource) {
        if (progressBar == null) {
            progressBar = ProgressBar.INSTANCE.getResource(eventSource);
            if (progressBar != null) {
                progressBarOwner = eventSource;
            }
        }
    }

    private void setEventToProgressBar(final ProgressEvent evt) {
        if (progressBar != null) {
            EventQueueUtil.invokeInDispatchThread(new Runnable() {

                @Override
                public void run() {
                    synchronized (monitor) {
                        if (evt.isIndeterminate()) {
                            progressBar.setIndeterminate(true);
                        } else {
                            progressBar.setMinimum(evt.getMinimum());
                            progressBar.setMaximum(evt.getMaximum());
                            progressBar.setValue(evt.getValue());
                        }
                        progressBar.setStringPainted(evt.isStringPainted());
                        progressBar.setString(evt.getStringToPaint());
                    }
                }
            });
        }
    }
}
