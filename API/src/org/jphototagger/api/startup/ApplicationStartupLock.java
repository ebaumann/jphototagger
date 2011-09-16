package org.jphototagger.api.startup;

/**
 *
 * @author Elmar Baumann
 */
public interface ApplicationStartupLock {

    boolean forceLock();

    boolean isLocked();

    boolean lockApplication();

    boolean unlockApplication();
}
