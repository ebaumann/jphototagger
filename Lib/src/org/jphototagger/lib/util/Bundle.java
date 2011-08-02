package org.jphototagger.lib.util;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Elmar Baumann
 */
public class Bundle {

    /**
     * Returns a string from the resource bundle {@code Bundle.properties} within the same package
     * and logs {@link MissingResourceException}s rather than throwing it.
     *
     * @param clazz
     * @param key
     * @param params optional params as described in the {@link MessageFormat} class documentation
     * @return       Localized String in {@code Bundle.properties} in the same package as {@code clazz}
     */
    public static String getString(Class<?> clazz, String key, Object... params) {
        if (clazz == null) {
            throw new NullPointerException("clazz == null");
        }

        if (key == null) {
            throw new NullPointerException("key == null");
        }

        // Javadoc: Resource bundle instances created by the getBundle factory methods are cached by default
        String bundlePath = createDefaultBundlePath(clazz);
        ResourceBundle bundle = ResourceBundle.getBundle(bundlePath);

        return getFormattedString(bundle, key, params);
    }

    private static String createDefaultBundlePath(Class<?> clazz) {
        String packagePath = resolvePackagePath(clazz);
        String bundlePath = packagePath + '/' + "Bundle";

        return bundlePath;
    }

    private static String resolvePackagePath(Class<?> clazz) {
        String className = clazz.getName();
        int indexLastDot = className.lastIndexOf('.');

        if (indexLastDot < 1) {
            return "";
        }

        String packagePath = className.substring(0, indexLastDot);

        return packagePath.replace(".", "/");
    }

    private static String getFormattedString(ResourceBundle bundle, String key, Object... params) {

        try {
            String s = bundle.getString(key);

            return params == null || params.length == 0 ? s : MessageFormat.format(s, params);
        } catch (Exception ex) {
            Logger.getLogger(Bundle.class.getName()).log(Level.SEVERE, null, ex);

            return "?" + key + "?";
        }
    }

    private Bundle() {
    }
}
