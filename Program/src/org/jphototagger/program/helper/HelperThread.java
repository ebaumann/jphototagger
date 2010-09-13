/*
 * @(#)HelperThread.java    Created on 2010-01-02
 *
 * Copyright (C) 2009-2010 by the JPhotoTagger developer team.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package org.jphototagger.program.helper;

import org.jphototagger.lib.concurrent.Cancelable;
import org.jphototagger.program.event.listener.ProgressListener;
import org.jphototagger.program.event.ProgressEvent;
import org.jphototagger.program.view.panels.ProgressBar;

import java.util.HashSet;
import java.util.Set;

import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

/**
 * Base class for helper threads managing progress listeners and providing a
 * progress bar.
 *
 * @author  Elmar Baumann
 */
public abstract class HelperThread extends Thread implements Cancelable {
    private String                      info;
    private final Set<ProgressListener> prLs = new HashSet<ProgressListener>();
    private JProgressBar                progressBar;
    private volatile boolean            customProgressBar;
    private volatile boolean            infoChanged;
    private volatile int                minimum;
    private volatile int                maximum;
    private final Object                pBarOwner = this;

    public HelperThread() {
    }

    public HelperThread(String name) {
        super(name);
    }

    /**
     * Adds a progress listener.
     * <p>
     * On {@link #progressStarted(int, int, int, java.lang.Object)},
     * {@link #progressPerformed(int, java.lang.Object)} and
     * {@link #progressEnded(java.lang.Object)} all progress listeners will
     * be notified through the apporpriate progress listener interface method.
     * <p>
     * If a progress listener calls {@link ProgressEvent#cancel()},
     * {@link #cancel()} will called.
     *
     * @param listener progress listener
     */
    public void addProgressListener(ProgressListener listener) {
        if (listener == null) {
            throw new NullPointerException("listener == null");
        }

        synchronized (prLs) {
            prLs.add(listener);
        }
    }

    /**
     * Sets a custom progress bar.
     * <p>
     * If no progress bar is set, {@link ProgressBar} will be used.
     *
     * @param progressBar progress bar
     */
    public synchronized void setProgressBar(JProgressBar progressBar) {
        if (progressBar == null) {
            throw new NullPointerException("progressBar == null");
        }

        this.progressBar  = progressBar;
        customProgressBar = true;
    }

    /**
     * Sets an information text.
     * <p>
     * This text will be set as progress bar string.
     *
     * @param info info
     */
    public synchronized void setInfo(String info) {
        if (info == null) {
            throw new NullPointerException("info == null");
        }

        this.info   = info;
        infoChanged = true;
    }

    /**
     * Removes a progress listener.
     *
     * @param listener progress listener
     */
    public void removeProgressListener(ProgressListener listener) {
        if (listener == null) {
            throw new NullPointerException("listener == null");
        }

        synchronized (prLs) {
            prLs.remove(listener);
        }
    }

    private void notifyProgressStarted(ProgressEvent evt) {
        synchronized (prLs) {
            for (ProgressListener listener : prLs) {
                listener.progressStarted(evt);

                if (evt.isCancel()) {
                    cancel();
                }
            }
        }
    }

    private void notifyProgressPerformed(ProgressEvent evt) {
        synchronized (prLs) {
            for (ProgressListener listener : prLs) {
                listener.progressPerformed(evt);

                if (evt.isCancel()) {
                    cancel();
                }
            }
        }
    }

    private void notifyProgressEnded(ProgressEvent evt) {
        synchronized (prLs) {
            for (ProgressListener listener : prLs) {
                listener.progressEnded(evt);
            }
        }
    }

    private void getProgressBar() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (progressBar == null) {
                    progressBar = ProgressBar.INSTANCE.getResource(pBarOwner);

                    if (progressBar != null) {
                        progressBar.setIndeterminate(false);
                    }
                }
            }
        });
    }

    private void setProgressBar(final int value) {
        getProgressBar();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (progressBar != null) {
                    if (infoChanged && (info != null)) {
                        if (!progressBar.isStringPainted()) {
                            progressBar.setStringPainted(true);
                        }

                        progressBar.setString(info);
                        infoChanged = false;
                    }

                    progressBar.setMinimum(minimum);
                    progressBar.setMaximum(maximum);
                    progressBar.setValue(value);
                }
            }
        });
    }

    private ProgressEvent progressEvent(int value, Object info) {
        setProgressBar(value);

        return new ProgressEvent(this, minimum, maximum, value, info);
    }

    /**
     * Notifies all progress listeners that the progress has been started and
     * updates the progress bar.
     *
     * @param minimum minium value
     * @param value   current value
     * @param maximum maximum value
     * @param info    null or object set as
     *                {@link ProgressEvent#setInfo(Object)}
     */
    protected void progressStarted(int minimum, int value, int maximum,
                                   Object info) {
        this.minimum = minimum;
        this.maximum = maximum;
        notifyProgressStarted(progressEvent(value, info));
    }

    /**
     * Notifies all progress listeners that the progress has been performed and
     * updates the progress bar.
     *
     * @param value current value
     * @param info  null or object set as {@link ProgressEvent#setInfo(Object)}
     */
    protected void progressPerformed(int value, Object info) {
        notifyProgressPerformed(progressEvent(value, info));
    }

    /**
     * Notifies all progress listeners that the progress has been ended and
     * updates the progress bar.
     *
     * @param info null or object set as {@link ProgressEvent#setInfo(Object)}
     */
    protected void progressEnded(Object info) {
        notifyProgressEnded(progressEvent(0, info));
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (progressBar != null) {
                    progressBar.setString("");
                    progressBar.setStringPainted(false);

                    if (!customProgressBar) {
                        ProgressBar.INSTANCE.releaseResource(pBarOwner);
                        progressBar = null;
                    }
                }
            }
        });
    }
}
