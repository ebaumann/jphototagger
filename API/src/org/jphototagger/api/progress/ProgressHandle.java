package org.jphototagger.api.progress;

/**
 * @author Elmar Baumann
 */
public interface ProgressHandle {

    void progressStarted(ProgressEvent evt);

    void progressPerformed(ProgressEvent evt);

    void progressEnded();
}
