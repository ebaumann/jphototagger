package org.jphototagger.plugin;

import org.jphototagger.services.plugin.FileProcessorPluginListener;
import org.jphototagger.services.plugin.FileProcessorPluginEvent;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.Set;
import javax.swing.JProgressBar;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.lib.util.ServiceLookup;
import org.jphototagger.services.core.ProgressBarProvider;
import org.jphototagger.services.plugin.FileProcessorPlugin;

/**
 * Handles Listeners and provides a progress bar.
 *
 * @author Elmar Baumann
 */
public abstract class AbstractFileProcessorPlugin implements FileProcessorPlugin {

    private JProgressBar progressBar;
    private boolean progressBarIsStringPainted;
    private final Set<FileProcessorPluginListener> fileProcessorPluginListeners = new CopyOnWriteArraySet<FileProcessorPluginListener>();

    public void addFileProcessorPluginListener(FileProcessorPluginListener listener) {
        if (listener == null) {
            throw new NullPointerException("listener == null");
        }

        fileProcessorPluginListeners.add(listener);
    }

    public void removeFileProcessorPluginListener(FileProcessorPluginListener listener) {
        if (listener == null) {
            throw new NullPointerException("listener == null");
        }

        fileProcessorPluginListeners.remove(listener);
    }

    protected void notifyFileProcessorPluginListeners(FileProcessorPluginEvent event) {
        if (event == null) {
            throw new NullPointerException("event == null");
        }

        if (fileProcessorPluginListeners.size() > 0) {
            for (FileProcessorPluginListener listener : fileProcessorPluginListeners) {
                listener.action(event);
            }
        }

        if (event.getType().isFinished()) {
            releaseProgressBar();
        }
    }

    private void getProgressBarFromService() {
        if (progressBar != null) {
            return;
        }

        ProgressBarProvider progressBarProvider = ServiceLookup.lookup(ProgressBarProvider.class);

        if (progressBarProvider != null) {
            progressBar = progressBarProvider.getProgressBar(this);
        }
    }

    private void releaseProgressBar() {
        if (progressBar == null) {
            return;
        }

        ProgressBarProvider progressBarProvider = ServiceLookup.lookup(ProgressBarProvider.class);

        if (progressBarProvider != null) {
            progressBarProvider.releaseProgressBar(progressBar, this);
        }

        progressBar = null;
    }

    /**
     * Paints the progress bar start event.
     *
     * @param minimum miniumum
     * @param maximum maximum
     * @param value   current value
     * @param string  string to paint onto progress bar or null
     */
    protected void progressStarted(int minimum, int maximum, int value, String string) {
        getProgressBarFromService();
        setProgressBar(0, maximum, value, string);
    }

    /**
     * Paints a progress bar progress event.
     *
     * @param minimum minimum
     * @param maximum maximum
     * @param value   current value
     * @param string  string to paint onto progress bar or null
     */
    protected void progressPerformed(int minimum, int maximum, int value, String string) {
        getProgressBarFromService();
        setProgressBar(minimum, maximum, value, string);
    }

    /**
     * Paints the progress bar end event.
     */
    protected void progressEnded() {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            public void run() {
                if (progressBar != null) {
                    if (progressBar.isStringPainted()) {
                        progressBar.setString("");
                    }

                    progressBar.setStringPainted(progressBarIsStringPainted);
                    progressBar.setValue(0);
                    releaseProgressBar();
                }
            }
        });
    }

    private void setProgressBar(final int minimum, final int maximum, final int value, final String string) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            public void run() {
                if (progressBar != null) {
                    progressBar.setMinimum(minimum);
                    progressBar.setMaximum(maximum);
                    progressBar.setValue(value);

                    if (string != null) {
                        progressBar.setStringPainted(true);
                        progressBar.setString(string);
                    }
                }
            }
        });
    }
}
