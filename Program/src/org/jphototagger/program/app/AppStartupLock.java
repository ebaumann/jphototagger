package org.jphototagger.program.app;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openide.util.Lookup;

import org.jphototagger.api.storage.UserFilesProvider;
import org.jphototagger.lib.dialog.MessageDisplayer;
import org.jphototagger.lib.io.FileUtil;
import org.jphototagger.lib.util.Bundle;

/**
 * Creates an application lock file to prevent multiple instances. Uses
 * {@code org.jphototagger.program.UserSettings#getSettingsDirectoryName()}
 * to get the setting's directory where the lock file will be created.
 *
 * @author Elmar Baumann
 */
public final class AppStartupLock {

    private static final String LOCKFILE_NAME;

    static {
        UserFilesProvider provider = Lookup.getDefault().lookup(UserFilesProvider.class);
        File databaseDirectory = provider.getDatabaseDirectory();
        String databaseDirectoryName = databaseDirectory.getAbsolutePath();

        LOCKFILE_NAME = databaseDirectoryName + File.separator + AppInfo.PROJECT_NAME + ".lck";
    }

    private AppStartupLock() {
    }

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
                Logger.getLogger(AppStartupLock.class.getName()).log(Level.SEVERE, null, ex);
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
        String message = Bundle.getString(AppStartupLock.class, "AppStartupLock.Error.LockFileExists", LOCKFILE_NAME);

        return MessageDisplayer.confirmYesNo(null, message);
    }

    private static void errorMessageDelete() {
        String message = Bundle.getString(AppStartupLock.class, "AppStartupLock.Error.DeleteLockFile", LOCKFILE_NAME);

        MessageDisplayer.error(null, message);
    }
}
