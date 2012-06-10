package org.jphototagger.program.app;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openide.util.Lookup;

import org.jphototagger.domain.repository.FileRepositoryProvider;
import org.jphototagger.lib.io.FileUtil;
import org.jphototagger.lib.swing.MessageDisplayer;
import org.jphototagger.lib.util.Bundle;

/**
 * Creates an application lock file to prevent multiple instances.
 *
 * @author Elmar Baumann
 */
public final class AppStartupLock {

    private static final String LOCKFILE_NAME;
    private static boolean hasUnlockPrivilege;

    static {
        FileRepositoryProvider provider = Lookup.getDefault().lookup(FileRepositoryProvider.class);
        File repositoryDirectory = provider.getFileRepositoryDirectory();
        String repositoryDirectoryName = repositoryDirectory.getAbsolutePath();
        LOCKFILE_NAME = repositoryDirectoryName + File.separator + AppInfo.PROJECT_NAME + ".lck";
        addShutdownCleanupCheck();
    }

    private AppStartupLock() {
    }

    public static synchronized boolean isLocked() {
        return FileUtil.existsFile(new File(LOCKFILE_NAME));
    }

    public static synchronized boolean lock() {
        if (!isLocked()) {
            try {
                FileUtil.ensureFileExists(new File(LOCKFILE_NAME));
                hasUnlockPrivilege = true;
                return true;
            } catch (IOException ex) {
                Logger.getLogger(AppStartupLock.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return false;
    }

    public static synchronized boolean unlock() {
        if (hasUnlockPrivilege && isLocked()) {
            return new File(LOCKFILE_NAME).delete();
        }
        return true;
    }

    /**
     * Displays an error message that the lock file couldn't be created and offers the option to unlock - delete - it.
     *
     * @return true if unlocked
     */
    public static synchronized boolean forceLock() {
        if (confirmForceUnlock()) {
            return deleteLockFile() && lock();
        }
        return false;
    }

    private static boolean deleteLockFile() {
        if (!FileUtil.existsFile(new File(LOCKFILE_NAME))) {
            return true;
        }
        if (new File(LOCKFILE_NAME).delete()) {
            return true;
        } else {
            errorMessageDelete();
            return false;
        }
    }

    private static boolean confirmForceUnlock() {
        String message = Bundle.getString(AppStartupLock.class, "AppStartupLock.Error.LockFileExists", LOCKFILE_NAME);
        return MessageDisplayer.confirmYesNo(null, message);
    }

    private static void errorMessageDelete() {
        String message = Bundle.getString(AppStartupLock.class, "AppStartupLock.Error.DeleteLockFile", LOCKFILE_NAME);
        MessageDisplayer.error(null, message);
    }

    private static void addShutdownCleanupCheck() {
        Runtime.getRuntime().addShutdownHook(new Thread() {

            @Override public void run() {
                unlock();
            }
        });
    }
}
