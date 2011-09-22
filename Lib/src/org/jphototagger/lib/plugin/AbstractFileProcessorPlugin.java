package org.jphototagger.lib.plugin;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JProgressBar;

import org.openide.util.Lookup;

import org.jphototagger.api.plugin.fileprocessor.FileProcessorPlugin;
import org.jphototagger.api.progress.MainWindowProgressBarProvider;
import org.jphototagger.lib.awt.EventQueueUtil;

/**
 * Handles Listeners and provides a progress bar.
 *
 * @author Elmar Baumann
 */
public abstract class AbstractFileProcessorPlugin implements FileProcessorPlugin {

    private JProgressBar progressBar;
    private boolean progressBarIsStringPainted;

    /**
     *
     * @return null
     */
    @Override
    public Icon getSmallIcon() {
        return null;
    }

    /**
     *
     * @return null
     */
    @Override
    public Icon getLargeIcon() {
        return null;
    }

    /**
     *
     * @return null
     */
    @Override
    public Component getSettingsComponent() {
        return null;
    }

    /**
     *
     * @return null
     */
    @Override
    public String getHelpContentsPath() {
        return null;
    }

    /**
     *
     * @return null
     */
    @Override
    public String getFirstHelpPageName() {
        return null;
    }

    private void getProgressBarFromService() {
        if (progressBar != null) {
            return;
        }

        MainWindowProgressBarProvider progressBarProvider = Lookup.getDefault().lookup(MainWindowProgressBarProvider.class);

        if (progressBarProvider != null) {
            progressBar = progressBarProvider.getProgressBar(this);
        }
    }

    protected void releaseProgressBar() {
        if (progressBar == null) {
            return;
        }

        MainWindowProgressBarProvider progressBarProvider = Lookup.getDefault().lookup(MainWindowProgressBarProvider.class);

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
