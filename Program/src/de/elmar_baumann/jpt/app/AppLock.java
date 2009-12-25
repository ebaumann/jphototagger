/*
 * JPhotoTagger tags and finds images fast
 * Copyright (C) 2009 by the developer team, resp. Elmar Baumann<eb@elmar-baumann.de>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package de.elmar_baumann.jpt.app;

import de.elmar_baumann.jpt.UserSettings;
import de.elmar_baumann.lib.io.FileUtil;
import java.io.File;

/**
 * Creates an application lock file to prevent multiple instances. Uses
 * {@link de.elmar_baumann.jpt.UserSettings#getSettingsDirectoryName()}
 * to get the setting's directory where the lock file will be created.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-10-10
 */
public final class AppLock {

    private static final String LOCKFILE_NAME =
            UserSettings.INSTANCE.getDatabaseDirectoryName() + File.separator +
            AppInfo.PROJECT_NAME + ".lck";

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
            return FileUtil.ensureFileExists(new File(LOCKFILE_NAME));
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
        if (new File(LOCKFILE_NAME).delete()) {
            return true;
        } else {
            errorMessageDelete();
            return false;
        }
    }

    private static boolean confirmForceUnlock() {
        return MessageDisplayer.confirm(null,
                "AppLock.Error.LockFileExists",
                MessageDisplayer.CancelButton.HIDE, LOCKFILE_NAME).equals(
                MessageDisplayer.ConfirmAction.YES);
    }

    private static void errorMessageDelete() {
        MessageDisplayer.error(null, "AppLock.Error.DeleteLockFile");
    }

    private AppLock() {
    }
}
