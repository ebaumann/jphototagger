package org.jphototagger.api.progress;

/**
 * An implementation of this interface should invoke GUI operations in the
 * Event Dispatch thread, so that a ProgressHandle can be called in other
 * threads.
 *
 * @author Elmar Baumann
 */
public interface ProgressHandle {

    void progressStarted(ProgressEvent evt);

    void progressPerformed(ProgressEvent evt);

    void progressEnded();
}
