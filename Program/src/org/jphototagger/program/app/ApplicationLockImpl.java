package org.jphototagger.program.app;

import org.jphototagger.api.startup.ApplicationStartupLock;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 *
 * @author Elmar Baumann
 */
@ServiceProvider(service = ApplicationStartupLock.class)
public final class ApplicationLockImpl implements ApplicationStartupLock {

    @Override
    public boolean forceLock() {
        return AppLock.forceLock();
    }

    @Override
    public boolean isLocked() {
        return AppLock.isLocked();
    }

    @Override
    public boolean lockApplication() {
        return AppLock.lock();
    }

    @Override
    public boolean unlockApplication() {
        return AppLock.unlock();
    }
}
