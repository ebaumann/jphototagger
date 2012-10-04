package org.jphototagger.lib.concurrent;

import java.util.HashSet;
import java.util.Set;
import org.jphototagger.api.concurrent.Cancelable;
import org.jphototagger.api.progress.ProgressEvent;
import org.jphototagger.api.progress.ProgressHandle;
import org.jphototagger.api.progress.ProgressHandleFactory;
import org.jphototagger.api.progress.ProgressListener;
import org.openide.util.Lookup;

/**
 * Base class for helper threads, displays progress with {@code ProgressHandleFactory} when
 * calling one of the <code>progress...</code>. methods and calling {@link Cancelable#cancel()} if
 * {@link ProgressEvent#isCancel()}.
 *
 * @author Elmar Baumann
 */
public abstract class HelperThread extends Thread implements Cancelable {

    private final Set<ProgressListener> progressListeners = new HashSet<ProgressListener>();
    private volatile Object info;
    private volatile int value;
    private volatile int minimum;
    private volatile int maximum;
    private ProgressHandle progressHandle;

    public HelperThread() {
    }

    public HelperThread(String name) {
        super(name);
    }

    /**
     *
     * @param listener will be notified when a <code>progress...</code> method was called
     */
    public void addProgressListener(ProgressListener listener) {
        if (listener == null) {
            throw new NullPointerException("listener == null");
        }
        synchronized (progressListeners) {
            progressListeners.add(listener);
        }
    }

    public void removeProgressListener(ProgressListener listener) {
        if (listener == null) {
            throw new NullPointerException("listener == null");
        }
        synchronized (progressListeners) {
            progressListeners.remove(listener);
        }
    }

    /**
     *
     * @param info <code>toString()</code> will be set as progress bar string
     */
    public synchronized void setInfo(String info) {
        this.info = info;
    }

    /**
     * Notifies all progress listeners that the progress has been started and
     * updates the progress bar.
     *
     * @param minimum minium value
     * @param value   current value
     * @param maximum maximum value
     * @param info    object to set as {@code ProgressEvent#setInfo(Object)}
     */
    protected void progressStarted(int minimum, int value, int maximum, Object info) {
        this.minimum = minimum;
        this.maximum = maximum;
        this.info = info;
        ProgressEvent evt = createProgressEvent();
        notifyProgressStarted(evt);
        progressHandle = Lookup.getDefault().lookup(ProgressHandleFactory.class).createProgressHandle(this);
        progressHandle.progressStarted(evt);
    }


    /**
     * Notifies all progress listeners that the progress has been performed and
     * updates the progress bar.
     *
     * @param value current value
     * @param info  object to set as {@code ProgressEvent#setInfo(Object)}
     */
    protected void progressPerformed(int value, Object info) {
        this.value = value;
        this.info = info;
        ProgressEvent evt = createProgressEvent();
        notifyProgressPerformed(evt);
        progressHandle.progressPerformed(evt);
    }

    /**
     * Notifies all progress listeners that the progress has been ended and
     * updates the progress bar.
     *
     * @param info object to set as {@code ProgressEvent#setInfo(Object)}
     */
    protected void progressEnded(Object info) {
        ProgressEvent evt = createProgressEvent();
        notifyProgressEnded(evt);
        progressHandle.progressEnded();
    }

    private ProgressEvent createProgressEvent() {
        return new ProgressEvent.Builder()
                .source(this)
                .minimum(minimum)
                .maximum(maximum)
                .value(value)
                .info(info)
                .stringPainted(isProgressBarStringPainted())
                .stringToPaint(getProgressBarString())
                .build();
    }

    private boolean isProgressBarStringPainted() {
        return info != null;
    }

    private String getProgressBarString() {
        return info == null
                ? null
                : info.toString();
    }

    private void notifyProgressStarted(ProgressEvent evt) {
        synchronized (progressListeners) {
            for (ProgressListener listener : progressListeners) {
                listener.progressStarted(evt);

                if (evt.isCancel()) {
                    cancel();
                }
            }
        }
    }

    private void notifyProgressPerformed(ProgressEvent evt) {
        synchronized (progressListeners) {
            for (ProgressListener listener : progressListeners) {
                listener.progressPerformed(evt);

                if (evt.isCancel()) {
                    cancel();
                }
            }
        }
    }

    private void notifyProgressEnded(ProgressEvent evt) {
        synchronized (progressListeners) {
            for (ProgressListener listener : progressListeners) {
                listener.progressEnded(evt);
            }
        }
    }
}
