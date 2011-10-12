package org.jphototagger.api.progress;

/**
 * @author Elmar Baumann
 */
public interface MainWindowProgressBarProvider {

    void progressStarted(ProgressEvent evt);

    void progressPerformed(ProgressEvent evt);

    void progressEnded(Object eventSource);

    boolean isDisplayProgressOfSource(Object eventSource);
}
