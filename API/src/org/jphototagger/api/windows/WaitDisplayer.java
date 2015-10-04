package org.jphototagger.api.windows;

/**
 * Implementations invoke the operations in the Event Dispatch Thread, so
 * that the WaitDisplayer can be used in ohter Threads.
 *
 * @author Elmar Baumann
 */
public interface WaitDisplayer {

    void show();
    void hide();
    boolean isShow();
}
