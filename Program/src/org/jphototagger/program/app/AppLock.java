package org.jphototagger.program.app;

import org.jphototagger.lib.io.FileUtil;
import org.jphototagger.program.UserSettings;
import java.io.File;
import java.io.IOException;

/**
 * Creates an application lock file to prevent multiple instances. Uses
 * {@link org.jphototagger.program.UserSettings#getSettingsDirectoryName()}
 * to get the setting's directory where the lock file will be created.
 *
 * @author Elmar Baumann
 */
public final class AppLock {
    private static final String LOCKFILE_NAME = UserSettings.INSTANCE.getDatabaseDirectoryName() + File.separator
                                                + AppInfo.PROJECT_NAME + ".lck";

    private AppLock() {}

    /**
     * Returns whether the application ist locked.
     *
     * @return  true if locked
     */
    public static synchronized boolean isLocked() {
        return FileUtil.existsFile(new File(LOCKFILE_NAME));
    }

    /**
     * Locks the application.
     *
     * @return true if locked
     */
    public static synchronized boolean lock() {
        if (!isLocked()) {
            try {
                FileUtil.ensureFileExists(new File(LOCKFILE_NAME));

                return true;
            } catch (IOException ex) {
                AppLogger.logSevere(AppLock.class, ex);
            }
        }

        return false;
    }

    /**
     * Unlocks the application. Typically called before exiting the VM.
     *
     * @return true if successful
     */
    public static synchronized boolean unlock() {
        if (isLocked()) {
            return new File(LOCKFILE_NAME).delete();
        }

        return true;
    }

    /**
     * Displays an error message that the lock file couldn't be created and
     * offers the option to unlock - delete - it.
     *
     * @return  true if unlocked
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
        return MessageDisplayer.confirmYesNo(null, "AppLock.Error.LockFileExists", LOCKFILE_NAME);
    }

    private static void errorMessageDelete() {
        MessageDisplayer.error(null, "AppLock.Error.DeleteLockFile", LOCKFILE_NAME);
    }
}
