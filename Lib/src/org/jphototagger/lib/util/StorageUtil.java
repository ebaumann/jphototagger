package org.jphototagger.lib.util;

import java.io.File;
import org.jphototagger.api.storage.Storage;

/**
 *
 * @author Elmar Baumann
 */
public final class StorageUtil {

    /**
     * Sets to the properties as value the absolute path of a directory.
     *
     * @param storage
     * @param key        key
     * @param file       file or null. If the file is not a directory, it's
     *                   parent file will be setTree. If the file is a directory, it
     *                   will be setTree. If the file is null, nothing will be setTree.
     * @return           true if the directory path name was setTree
     */
    public static boolean setDirectory(Storage storage, String key, File file) {
        if (storage == null) {
            throw new NullPointerException("storage == null");
        }

        if (key == null) {
            throw new NullPointerException("key == null");
        }

        if (file == null) {
            return false;
        }

        if (file.isDirectory()) {
            storage.setString(key, file.getAbsolutePath());

            return true;
        } else {
            storage.setString(key, file.getParentFile().getAbsolutePath());

            return true;
        }
    }

    /**
     *
     * @param clazz
     * @return Path <em>without</em> leading and trailing slash,
     *        e.g. {@code "org/jphototagger/bla"} if the Class name is {@code "org.jphototagger.bla.Blubb"}
     */
    public static String resolvePackagePathForResource(Class<?> clazz) {
        String className = clazz.getName();
        int indexLastDot = className.lastIndexOf('.');

        if (indexLastDot < 1) {
            return "";
        }

        String packagePath = className.substring(0, indexLastDot);

        return packagePath.replace(".", "/");
    }

    private StorageUtil() {
    }
}
