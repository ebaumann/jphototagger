package org.jphototagger.program.view.panels;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    private long lastProgressBarAccessInMilliseconds;
    private static final long WARN_ON_P_BAR_ACCESS_DELAY_IN_MILLISECONDS = 60000;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(threadFactory);
    private static final Logger LOGGER = Logger.getLogger(ProgressBarProviderImpl.class.getName());

    public ProgressBarProviderImpl() {
        scheduler.scheduleWithFixedDelay(warnOnLongProgressBarAccessDelays, 0,
                WARN_ON_P_BAR_ACCESS_DELAY_IN_MILLISECONDS, TimeUnit.MILLISECONDS);
    }

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
    public void progressEnded(final Object eventSource) {
        synchronized (monitor) {
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
                            lastProgressBarAccessInMilliseconds = System.currentTimeMillis();
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
                        lastProgressBarAccessInMilliseconds = System.currentTimeMillis();
                    }
                }
            });
        }
    }

    private final Runnable warnOnLongProgressBarAccessDelays = new Runnable() {

        @Override
        public void run() {
            synchronized(monitor) {
                if (progressBarOwner == null) {
                    return;
                }

                long nowInMilliseconds = System.currentTimeMillis();
                long delayInMilliseconds = nowInMilliseconds - lastProgressBarAccessInMilliseconds;

                if (delayInMilliseconds > WARN_ON_P_BAR_ACCESS_DELAY_IN_MILLISECONDS) {
                    LOGGER.log(Level.WARNING,
                            "Progress bar owner ''{0}'' last accessed the progressbar before {1} seconds",
                            new Object[]{progressBarOwner, delayInMilliseconds / 1000});
                }
            }
        }
    };

    private static final ThreadFactory threadFactory = new ThreadFactory() {

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);

            thread.setName("JPhotoTagger: Progress Bar Blocking Check");

            return thread;
        }
    };
}
