package de.elmar_baumann.imv;

import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.lib.io.FileUtil;
import de.elmar_baumann.lib.persistence.PersistentSettings;
import java.io.File;
import java.text.MessageFormat;
import javax.swing.JOptionPane;

/**
 * Creates an application lock file to prevent multiple instances. Uses
 * {@link de.elmar_baumann.lib.persistence.PersistentSettings}
 * to get the settings directory where the lock file will be created.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/10
 */
public final class AppLock {

    private static final String lockFileName =
        PersistentSettings.getInstance().getDirectoryName() + File.separator +
        PersistentSettings.getInstance().getAppName() + ".lck"; // NOI18N

    /**
     * Returns whether the application ist locked.
     * 
     * @return  true if locked
     */
    public synchronized static boolean isLocked() {
        return FileUtil.existsFile(lockFileName);
    }

    /**
     * Locks the application. Typically called after
     * {@link de.elmar_baumann.lib.persistence.PersistentSettings#setAppName(java.lang.String)}.
     * 
     * @return true if locked
     */
    public synchronized static boolean lock() {
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
    public synchronized static boolean unlock() {
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
    public static boolean forceUnlock() {
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
        MessageFormat msg = new MessageFormat(Bundle.getString("Lock.ErrorMessage.LockFileExists"));
        return JOptionPane.showConfirmDialog(
                null,
                msg.format(new Object[]{lockFileName}),
                Bundle.getString("Lock.ErrorMessage.LockFileExists.Title"),
                JOptionPane.YES_NO_OPTION,
                JOptionPane.ERROR_MESSAGE,
                AppSettings.getMediumAppIcon()) == JOptionPane.YES_OPTION;
    }

    private static void errorMessageDelete() {
        JOptionPane.showMessageDialog(
                null,
                Bundle.getString("Lock.ErrorMessage.DeleteLockFile"),
                Bundle.getString("Lock.ErrorMessage.DeleteLockFile.Title"),
                JOptionPane.ERROR_MESSAGE,
                AppSettings.getMediumAppIcon());
    }

    private AppLock() {}
}
