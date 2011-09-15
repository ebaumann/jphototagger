package org.jphototagger.plugin;

import javax.swing.JProgressBar;

import org.jphototagger.api.plugin.fileprocessor.FileProcessorPlugin;
import org.jphototagger.api.progress.ProgressBarProvider;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.openide.util.Lookup;

/**
 * Handles Listeners and provides a progress bar.
 *
 * @author Elmar Baumann
 */
public abstract class AbstractFileProcessorPlugin implements FileProcessorPlugin {

    private JProgressBar progressBar;
    private boolean progressBarIsStringPainted;

    private void getProgressBarFromService() {
        if (progressBar != null) {
            return;
        }

        ProgressBarProvider progressBarProvider = Lookup.getDefault().lookup(ProgressBarProvider.class);

        if (progressBarProvider != null) {
            progressBar = progressBarProvider.getProgressBar(this);
        }
    }

    protected void releaseProgressBar() {
        if (progressBar == null) {
            return;
        }

        ProgressBarProvider progressBarProvider = Lookup.getDefault().lookup(ProgressBarProvider.class);

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
