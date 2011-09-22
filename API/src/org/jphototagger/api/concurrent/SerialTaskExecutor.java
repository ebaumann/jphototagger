package org.jphototagger.api.concurrent;

/**
 * Runs tasks (runnables) in a thread, starts a new thread (only)
 * after the previous task has been finished.
 *
 * @author Elmar Baumann
 */
public interface SerialTaskExecutor {

    void addTask(Runnable runnable);

    int getTaskCount();

    void cancelAllTasks();
}
