package org.jphototagger.api.progress;

import javax.swing.JProgressBar;

/**
 *
 *
 * @author Elmar Baumann
 */
public interface MainWindowProgressBarProvider {

    /**
     *
     * @param  owner owner who can release that progress bar
     * @return       Progress bar or null
     */
    JProgressBar getProgressBar(Object owner);

    /**
     *
     * @param  progressBar
     * @param  owner only the owner got that progress bar can release the progress bar
     * @return true if released
     */
    boolean releaseProgressBar(JProgressBar progressBar, Object owner);
}
