package de.elmar_baumann.imv.tasks;

/**
 * A Task.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/02/17
 */
public interface Task {

    /**
     * Starts the task.
     */
    public void start();

    /**
     * Stops the task.
     */
    public void stop();
}
