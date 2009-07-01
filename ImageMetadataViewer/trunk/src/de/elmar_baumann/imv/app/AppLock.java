package de.elmar_baumann.imv.app;

import de.elmar_baumann.imv.UserSettings;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.lib.io.FileUtil;
import java.io.File;
import javax.swing.JOptionPane;

/**
 * Creates an application lock file to prevent multiple instances. Uses
 * {@link de.elmar_baumann.imv.UserSettings#getSettingsDirectoryName()}
 * to get the setting's directory where the lock file will be created.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/10
 */
public final class AppLock {

    private static final String lockFileName =
        UserSettings.INSTANCE.getDatabaseDirectoryName() + File.separator +
        AppInfo.getProjectName() + ".lck"; // NOI18N

    /**
     * Returns whether the application ist locked.
     * 
     * @return  true if locked
     */
    public static synchronized boolean isLocked() {
        return FileUtil.existsFile(lockFileName);
    }

    /**
     * Locks the application.
     * 
     * @return true if locked
     */
    public static synchronized boolean lock() {
        if (!isLocked()) {
            return FileUtil.ensureFileExists(lockFileName);
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
            return new File(lockFileName).delete();
        }
        return true;
    }

    /**
     * Displays an error message that the lock file couldn't be created and offers
     * the option to unlock - delete - it.
     * 
     * @return  true if unlocked
     */
    public static synchronized boolean forceUnlock() {
        if (confirmForceUnlock()) {
            return deleteLockFile();
        }
        return false;
    }

    private static boolean deleteLockFile() {
        if (new File(lockFileName).delete()) {
            return true;
        } else {
            errorMessageDelete();
            return false;
        }
    }

    private static boolean confirmForceUnlock() {
        return JOptionPane.showConfirmDialog(
                null,
                Bundle.getString("AppLock.ErrorMessage.LockFileExists", lockFileName),
                Bundle.getString("AppLock.ErrorMessage.LockFileExists.Title"),
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION;
    }

    private static void errorMessageDelete() {
        JOptionPane.showMessageDialog(
                null,
                Bundle.getString("AppLock.ErrorMessage.DeleteLockFile"),
                Bundle.getString("AppLock.ErrorMessage.DeleteLockFile.Title"),
                JOptionPane.ERROR_MESSAGE);
    }

    private AppLock() {}
}
