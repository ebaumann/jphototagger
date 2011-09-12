package org.jphototagger.api.core;

/**
 *
 *
 * @author Elmar Baumann
 */
public interface ApplicationLock {

    boolean forceLock();

    boolean isLocked();

    boolean lockApplication();

    boolean unlockApplication();
}
