package org.jphototagger.program.app;

import org.jphototagger.api.startup.ApplicationStartupLock;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 *
 * @author Elmar Baumann
 */
@ServiceProvider(service = ApplicationStartupLock.class)
public final class ApplicationStartupLockImpl implements ApplicationStartupLock {

    @Override
    public boolean forceLock() {
        return AppStartupLock.forceLock();
    }

    @Override
    public boolean isLocked() {
        return AppStartupLock.isLocked();
    }

    @Override
    public boolean lockApplication() {
        return AppStartupLock.lock();
    }

    @Override
    public boolean unlockApplication() {
        return AppStartupLock.unlock();
    }
}
