package org.jphototagger.api.event;

/**
 *
 * @author Elmar Baumann
 */
public interface ProgressListener {

    void progressStarted(ProgressEvent evt);

    void progressPerformed(ProgressEvent evt);

    void progressEnded(ProgressEvent evt);
}
