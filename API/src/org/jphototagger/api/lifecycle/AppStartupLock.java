package org.jphototagger.api.lifecycle;

/**
 * @author Elmar Baumann
 */
public interface AppStartupLock {

    boolean forceStartupLock();

    boolean isStartupLocked();

    boolean lockStartup();

    boolean unlockStartup();
}
