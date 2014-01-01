package org.jphototagger.lib.util;

import java.io.File;
import org.jphototagger.api.preferences.Preferences;

/**
 * @author Elmar Baumann
 */
public final class PreferencesUtil {

    /**
     * Sets to a Preferences instance as value the absolute path of a directory, resolves the directory of a file.
     *
     * @param prefs
     * @param key key
     * @param file file or null. If the file is not a directory, it's parent file will be setTree. If the file is a
     * directory, it will be setTree. If the file is null, nothing will be setTree.
     * @return true if the directory path name was setTree
     */
    public static boolean setDirectory(Preferences prefs, String key, File file) {
        if (prefs == null) {
            throw new NullPointerException("prefs == null");
        }
        if (key == null) {
            throw new NullPointerException("key == null");
        }
        if (file == null) {
            return false;
        }
        if (file.isDirectory()) {
            prefs.setString(key, file.getAbsolutePath());
            return true;
        } else {
            prefs.setString(key, file.getParentFile().getAbsolutePath());
            return true;
        }
    }

    private PreferencesUtil() {
    }
}
