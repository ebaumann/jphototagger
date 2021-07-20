package org.jphototagger.api.applifecycle;

/**
 * Task called before the JVM exits.
 *
 * @author Elmar Baumann
 */
public interface AppExitTask {

    void execute();
}
