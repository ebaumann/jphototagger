package org.jphototagger.api.applifecycle;

/**
 * @author Elmar Baumann
 */
public interface AppStartupLock {

    boolean forceStartupLock();

    boolean isStartupLocked();

    boolean lockStartup();

    boolean unlockStartup();
}
