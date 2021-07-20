package org.jphototagger.api.concurrent;

/**
 * Starts a task (runnable) in a thread as long as a newer
 * task shall be executed.
 *
 * @author Elmar Baumann
 */
public interface ReplaceableTask {

    void replacePreviousTaskWith(Runnable runnable);

    void cancelRunningTask();
}
