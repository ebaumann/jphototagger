package org.jphototagger.api.startup;

/**
 * @author Elmar Baumann
 */
public interface AppStartupLock {

    boolean forceStartupLock();

    boolean isStartupLocked();

    boolean lockStartup();

    boolean unlockStartup();
}
