package org.jphototagger.lib.util;

import java.io.File;
import java.util.Properties;

/**
 * Utils for {@link java.util.Properties}.
 *
 * @author Elmar Baumann
 */
public final class PropertiesUtil {

    /**
     * Sets to the properties as value the absolute path of a directory.
     *
     * @param properties properties
     * @param key        key
     * @param file       file or null. If the file is not a directory, it's
     *                   parent file will be set. If the file is a directory, it
     *                   will be set. If the file is null, nothing will be set.
     * @return           true if the directory path name was set
     */
    public static boolean setDirectory(Properties properties, String key, File file) {
        if (properties == null) {
            throw new NullPointerException("properties == null");
        }

        if (key == null) {
            throw new NullPointerException("key == null");
        }

        if (file == null) {
            return false;
        }

        if (file.isDirectory()) {
            properties.put(key, file.getAbsolutePath());

            return true;
        } else {
            properties.put(key, file.getParentFile().getAbsolutePath());

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

    private PropertiesUtil() {
    }
}
