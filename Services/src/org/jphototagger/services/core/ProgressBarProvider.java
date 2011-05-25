package org.jphototagger.services.core;

import javax.swing.JProgressBar;

/**
 *
 *
 * @author Elmar Baumann
 */
public interface ProgressBarProvider {

    /**
     *
     * @param  owner owner who can release that progress bar
     * @return       Progress bar or null
     */
    JProgressBar getProgressBar(Object owner);

    /**
     *
     * @param  progressBar
     * @param  owner       only the owner got that progress bar can release the progress bar
     * @return
     */
    boolean releaseProgressBar(JProgressBar progressBar, Object owner);
}
